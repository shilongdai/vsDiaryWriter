package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.journal.secureAlgs.BCDigester;
import net.viperfish.journal.secureAlgs.Digester;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * an authenticator that uses hashing algorithms to verify a password. It uses
 * an hashing algorithm specified by the user. Currently, the password is
 * combined with a salt and hashed together for 30,000 rounds. The salt would
 * then be stored with the hashed password in the format of {@link PasswordFile}
 * .
 * 
 * <p>
 * This class is NOT thread safe
 * </p>
 * 
 * 
 * @author sdai
 *
 */
final class HashAuthManager implements AuthenticationManager {

	public static final String HASH_ALG = "viperfish.auth.hash";

	private final Digester dig;
	private final IOFile passwdFile;
	private String password;
	private byte[] hash;
	private byte[] salt;
	private final SecureRandom rand;
	private boolean ready;

	/**
	 * hash a data with salt for a number of iteration
	 * 
	 * @param data
	 *            the bytes to hash
	 * @param salt
	 *            the salts to hash with the data
	 * @param iter
	 *            the number of iterations
	 * @return the hash result
	 */
	private byte[] hashWithSalt(byte[] data, byte[] salt, int iter) {
		// the initial buffer to put the combination of salt and data
		byte[] buffer = new byte[data.length + salt.length];
		byte[] initial;

		// copy the data and salt into the buffer
		System.arraycopy(data, 0, buffer, 0, data.length);
		System.arraycopy(salt, 0, buffer, data.length, salt.length);

		// calculate the first round of hash
		initial = dig.digest(buffer);

		// re-allocate the buffer so that it can contain the hash + the salt
		buffer = new byte[initial.length + salt.length];

		// for every round, calculates the hash from the buffer of previous
		// round, combines the hash with salt into the buffer for the next round
		byte[] hash = initial;
		for (int i = 0; i < iter - 1; ++i) {
			System.arraycopy(hash, 0, buffer, 0, hash.length);
			System.arraycopy(salt, 0, buffer, hash.length, salt.length);
			hash = dig.digest(buffer);
		}
		return hash;
	}

	/**
	 * generate a salt of specified length using a secure random number
	 * generator
	 * 
	 * @param length
	 *            the number of bytes to generate
	 */
	private void generateSalt(int length) {
		salt = new byte[length];
		rand.nextBytes(salt);
	}

	/**
	 * writes the hash and salt to the "passwd" file, the content written are
	 * UTF-8 characters
	 * 
	 * @param formatted
	 *            the password data content to write
	 */
	private void writePasswdFile(PasswordFile pFile) {
		try {
			passwdFile.write(pFile.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			FailToStoreCredentialException fail = new FailToStoreCredentialException(
					"Failed to write to password file:" + e.getMessage());
			fail.initCause(e);
			throw new RuntimeException(fail);
		}
	}

	/**
	 * read the hash and salt from the password file, the content read are UTF-8
	 * characters
	 * 
	 * @return the combination of hash and salt
	 */
	private PasswordFile readPasswdFile() {
		try {
			String combo = passwdFile.read(StandardCharsets.UTF_8);
			if (combo.length() != 0) {
				return PasswordFile.valueOf(combo);
			} else {
				return new PasswordFile(new byte[0], new byte[0]);
			}
		} catch (IOException | IllegalArgumentException e) {
			FailToLoadCredentialException f = new FailToLoadCredentialException(
					"Cannot read from password file:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * load the hash and salt from file into memory
	 */
	private void loadPasswd() {
		PasswordFile pFile = readPasswdFile();
		this.hash = pFile.getCredentialInfo();
		this.salt = pFile.getSalt();
		ready = true;
	}

	/**
	 * constructs a {@link HashAuthManager} with the directory of the password
	 * file
	 * 
	 * 
	 * @param dataDir
	 *            the directory of the password file
	 */
	public HashAuthManager(File dataDir) {
		dig = new BCDigester();
		dig.setMode(Configuration.getString(HASH_ALG));
		rand = new SecureRandom();
		ready = false;
		passwdFile = new IOFile(new File(dataDir.getPath() + "/passwd"), new TextIOStreamHandler());
	}

	/**
	 * empty the password file
	 * 
	 * This method clears the password file and resets all internal data to
	 * initial condition. It an exception occurs, it would be wrapped in a
	 * {@link RuntimeException} and thrown. If this method fails, all internal
	 * data would be cleared but the password file would be intact.
	 * 
	 * @throws IOException
	 *             if failed to clear the password file
	 */
	@Override
	public void clear() {
		try {
			this.hash = null;
			this.password = null;
			this.ready = false;
			this.salt = null;
			passwdFile.clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * sets the password
	 * 
	 * This method sets the plain text password, generates a salt, hashes the
	 * password with the salt for 300,000 rounds then stores it in the password
	 * file in the format of {@link PasswordFile}. If an error occurs, this
	 * object would be unusable until a call to this method or
	 * {@link HashAuthManager#reload()}(if password already set before without
	 * being cleared) succeeds. If an exception occurs, it would be wrapped by a
	 * {@link RuntimeException} and thrown.
	 * 
	 * @throws FailToStoreCredentialException
	 *             if failed to flush the credentials to password file
	 * 
	 * @param pass
	 *            the plain text password
	 */
	@Override
	public void setPassword(String pass) {
		this.password = pass;
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		generateSalt(12);
		hash = hashWithSalt(bytes, salt, 300000);
		writePasswdFile(new PasswordFile(hash, salt));
		ready = true;
	}

	/**
	 * reload the hash and salt from the file
	 * 
	 * This method reloads the hash and salt stored in the format of
	 * {@link PasswordFile} from the password file. It would leave the object in
	 * a state ready to verify if the password file contains valid data. If an
	 * error occurs, this object would not be in an usable state until a call to
	 * {@link HashAuthManager#reload()} or
	 * {@link HashAuthManager#setPassword(String)} succeeds. If an exception
	 * occurs, it would be wrapped in a {@link RuntimeException} and thrown.
	 * 
	 * @throws FailToLoadCredentialException
	 *             if failed to read the credentials from file or it is invalid
	 */
	@Override
	public void reload() {
		hash = null;
		salt = null;
		loadPasswd();
	}

	/**
	 * verifies if a password is valid
	 * 
	 * This method verifies whether a provided password is valid against a
	 * password previously set. A password must be set before either during
	 * runtime or stored in the file, or undefined behavior can happen. If an
	 * exception happens, it would be wrapped by a {@link RuntimeException} and
	 * thrown.
	 * 
	 * @throws FailToLoadCredentialException
	 *             if cannot load password information from the password file
	 * 
	 * @return true if password valid, false otherwise
	 */
	@Override
	public boolean verify(String pass) {
		if (!ready) {
			loadPasswd();
		}
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		byte[] providedHash = hashWithSalt(bytes, salt, 300000);

		if (Arrays.equals(providedHash, hash)) {
			this.password = pass;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * gets the plain text password
	 * 
	 * This method returns the plain text password set before. A call to
	 * {@link HashAuthManager#setPassword(String)} or a valid password to
	 * {@link HashAuthManager#verify(String)} must be called before this is
	 * usable. Otherwise, it will return null.
	 * 
	 * @return the plain text password or null
	 */
	@Override
	public String getPassword() {
		return password;
	}
}
