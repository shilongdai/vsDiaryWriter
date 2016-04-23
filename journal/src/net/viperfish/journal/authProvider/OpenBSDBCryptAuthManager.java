package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.OpenBSDBCrypt;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.errors.CannotClearPasswordException;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * An authentication manager that uses BCrypt to hash password.
 * 
 * This class uses BCrypt used on OpenBSD to hash user password with a 16 byte
 * salt. The hashing process takes a while to thwart brute force cracking. The
 * current cost factor for BCrypt is 16.
 * 
 * This class <b>IS</b> thread safe.
 * 
 * @author sdai
 *
 */
final class OpenBSDBCryptAuthManager implements AuthenticationManager {

	private IOFile passwdFile;
	private SecureRandom rand;
	private String current;
	private String password;

	/**
	 * creates an {@link OpenBSDBCryptAuthManager} given the location of the
	 * password storage file
	 * 
	 * @param passwdFile
	 *            the file to store password informations in
	 */
	public OpenBSDBCryptAuthManager(File passwdFile) {
		this.passwdFile = new IOFile(passwdFile, new TextIOStreamHandler());
		rand = new SecureRandom();
	}

	/**
	 * clears the content of the password file
	 * 
	 * This method clears the content of the password file by writing an empty
	 * string to the file.
	 * 
	 * @throws CannotClearPasswordException
	 *             if failed to write an empty string
	 * 
	 * 
	 */
	@Override
	public synchronized void clear() throws CannotClearPasswordException {
		try {
			passwdFile.write("", StandardCharsets.US_ASCII);

			this.current = null;
			this.password = null;
		} catch (IOException e) {
			CannotClearPasswordException cp = new CannotClearPasswordException(
					"Failed to clear password stored in the file:" + passwdFile.getFile() + ", message:"
							+ e.getMessage());
			cp.initCause(e);
			throw cp;
		}
	}

	/**
	 * sets a password
	 * 
	 * This method sets the password of the user in this
	 * {@link AuthenticationManager}. The password is hashed with BCrypt using
	 * the format in OpenBSD with a cost factor of 16. The final output will be
	 * written to the password file as an ASCII string. This method will ready
	 * this authenticator for {@link OpenBSDBCryptAuthManager#verify(String)}
	 * and {@link OpenBSDBCryptAuthManager#getPassword()}.
	 * 
	 * @param pass
	 *            the password to set
	 * 
	 * @throws FailToStoreCredentialException
	 *             if failed to write the hashed password to the password file
	 */
	@Override
	public synchronized void setPassword(String pass) throws FailToStoreCredentialException {
		byte[] salt = new byte[16];
		rand.nextBytes(salt);
		current = OpenBSDBCrypt.generate(pass.toCharArray(), salt, 16);
		try {
			passwdFile.write(current, StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToStoreCredentialException fc = new FailToStoreCredentialException(
					"Cannot store the hashed password:" + e.getMessage());
			fc.initCause(e);
			throw fc;
		}
		this.password = pass;
	}

	/**
	 * load the password hash from the password file
	 * 
	 * This method loads the credential in the password file into buffer. If the
	 * password file does not exists, it will be created. If the password exists
	 * and contains valid information, this method will ready the authenticator
	 * for {@link OpenBSDBCryptAuthManager#verify(String)}.
	 * 
	 * @throws FailToLoadCredentialException
	 *             if failed to read hash from password file
	 */
	@Override
	public synchronized void load() throws FailToLoadCredentialException {
		try {
			current = passwdFile.read(StandardCharsets.US_ASCII);
		} catch (IOException e) {
			FailToLoadCredentialException fl = new FailToLoadCredentialException(
					"Cannot load hashed password from file:" + e.getMessage());
			fl.initCause(e);
			throw fl;
		}

	}

	/**
	 * gets the set password
	 * 
	 * This method returns the valid password of the user. It can only return a
	 * valid result if the {@link OpenBSDBCryptAuthManager#setPassword(String)}
	 * or a valid {@link OpenBSDBCryptAuthManager#verify(String)} is called.
	 * 
	 * @return the valid user password if conditions met, otherwise null
	 */
	@Override
	public synchronized String getPassword() {
		return password;
	}

	/**
	 * verifies a password
	 * 
	 * This method verifies a password input. It can only be called after a
	 * successful {@link OpenBSDBCryptAuthManager#setPassword(String)} or a
	 * {@link OpenBSDBCryptAuthManager#load()} with valid password file. If
	 * the password is valid, the plain text password will be stored, which can
	 * be accessed by {@link OpenBSDBCryptAuthManager#getPassword()}.
	 * 
	 * @param pass
	 *            the password to verify
	 * @return true if valid, false otherwise
	 * 
	 * @throws NullPointerException
	 *             if no hash is in the buffer
	 */
	@Override
	public synchronized boolean verify(String pass) {
		boolean result = OpenBSDBCrypt.checkPassword(current, pass.toCharArray());
		if (result) {
			this.password = pass;
		}
		return result;
	}

}
