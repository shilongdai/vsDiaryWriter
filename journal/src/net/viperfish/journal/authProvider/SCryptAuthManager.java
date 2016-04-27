package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.generators.SCrypt;

import net.viperfish.framework.file.IOFile;
import net.viperfish.framework.file.TextIOStreamHandler;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;

/**
 * An {@link AuthenticationManager} using SCrypt to hash password.
 * 
 * This class uses SCrypt to hash password with 8 byte salt. The SCrypt is tuned
 * with N = 262144, r = 10, and p = 3. The final output hash is 256 bit.
 * 
 * This class <b>IS</b> thread safe.
 * 
 * @author sdai
 *
 */
final class SCryptAuthManager implements AuthenticationManager {

	private SecureRandom rand;
	private String password;
	private PasswordFile passCont;
	private IOFile passwdFile;

	/**
	 * creates an {@link SCryptAuthManager} given password file
	 * 
	 * This constructor creates an {@link SCryptAuthManager} given the location
	 * to a password file. The password file does not have to exists for this to
	 * work.
	 * 
	 * @param passwdFile
	 *            the location of the password file
	 */
	public SCryptAuthManager(File passwdFile) {
		rand = new SecureRandom();
		this.passwdFile = new IOFile(passwdFile, new TextIOStreamHandler());
		passCont = null;
	}

	/**
	 * clears the content of the password file
	 * 
	 * This method clears the content of the password file by writing an empty
	 * string to the file.
	 * 
	 * @throws IOException
	 *             if failed to write an empty string
	 */
	@Override
	public synchronized void clear() {
		try {
			passwdFile.write("", StandardCharsets.UTF_16);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * sets the password
	 * 
	 * This method sets the password of the user. The password is hashed with
	 * SCrypt. The hashed password will be stored with the salt in the format of
	 * the {@link PasswordFile} in the password file. A successful invocation of
	 * the method prepares the authenticator for
	 * {@link SCryptAuthManager#verify(String)} and
	 * {@link SCryptAuthManager#getPassword()}.
	 * 
	 * @param pass
	 *            the password to set
	 * 
	 * @throws FailToStoreCredentialException
	 *             if cannot write the generated hash to password file
	 */
	@Override
	public synchronized void setPassword(String pass) {
		byte[] salt = new byte[8];
		rand.nextBytes(salt);
		byte[] crypt = SCrypt.generate(pass.getBytes(StandardCharsets.UTF_16), salt, 262144, 10, 3, 256);
		passCont = new PasswordFile(crypt, salt);
		try {
			passwdFile.write(passCont.toString(), StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToStoreCredentialException fs = new FailToStoreCredentialException(
					"Cannot store password to passwd file:" + e.getMessage());
			fs.initCause(e);
			passCont = null;

			throw new RuntimeException(fs);
		}
		this.password = pass;
	}

	/**
	 * loads the hash from file
	 * 
	 * This method loads content in the password file into buffer. If the
	 * password file does not exists, it will be created. The password file must
	 * be either empty or contains hash data in the format of
	 * {@link PasswordFile}. This method readies the authenticator for
	 * {@link SCryptAuthManager#verify(String)} if the password file contains
	 * non empty valid password data.
	 * 
	 * @throws FailToLoadCredentialException
	 *             if cannot load hash from file
	 * @throws IllegalArgumentException
	 *             see {@link PasswordFile#valueOf(String)}
	 */
	@Override
	public synchronized void load() {
		try {
			String content = passwdFile.read(StandardCharsets.US_ASCII);
			if (content.length() == 0) {
				return;
			}
			passCont = PasswordFile.valueOf(content);
		} catch (IOException e) {
			FailToLoadCredentialException fl = new FailToLoadCredentialException(
					"Cannot load credential from file:" + e.getMessage());
			fl.initCause(e);
			throw new RuntimeException(fl);
		}
	}

	/**
	 * returns the plain text password
	 * 
	 * This method returns the plain text password if available. The plain text
	 * password is available after a successful
	 * {@link SCryptAuthManager#setPassword(String)} or a valid
	 * {@link SCryptAuthManager#verify(String)} is called.
	 * 
	 * @return the plain text password if available or null otherwise
	 */
	@Override
	public synchronized String getPassword() {
		return password;
	}

	/**
	 * verifies a password
	 * 
	 * This method verifies if a given password is the valid password. This must
	 * be called after a {@link SCryptAuthManager#load()} with valid non empty
	 * password file or a {@link SCryptAuthManager#setPassword(String)} is
	 * called. If the given password is valid, the plain text password will be
	 * stored and ready to be accessed via the
	 * {@link SCryptAuthManager#getPassword()}.
	 * 
	 * @param pass
	 *            the password to verify
	 * @return true if valid, false otherwise
	 * 
	 * @throws NullPointerException
	 *             if hashed password not in buffer
	 */
	@Override
	public synchronized boolean verify(String pass) {
		byte[] crypt = SCrypt.generate(pass.getBytes(StandardCharsets.UTF_16), passCont.getSalt(), 262144, 10, 3, 256);
		boolean match = Arrays.equals(crypt, passCont.getCredentialInfo());
		if (match) {
			this.password = pass;
		}
		return match;
	}

}
