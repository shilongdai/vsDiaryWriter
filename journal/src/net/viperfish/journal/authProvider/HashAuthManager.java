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
 * an authenticator that uses hashing algorithms to verify a password
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
				return PasswordFile.getPasswordData(combo);
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

	public HashAuthManager(File dataDir) {
		dig = new BCDigester();
		dig.setMode(Configuration.getString(HASH_ALG));
		rand = new SecureRandom();
		ready = false;
		passwdFile = new IOFile(new File(dataDir.getPath() + "/passwd"), new TextIOStreamHandler());
	}

	public byte[] getSalt() {
		return salt;
	}

	/**
	 * empty the password file
	 */
	@Override
	public void clear() {
		try {
			passwdFile.clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * generate a 12 byte salt, and hash the password(UTF-16) with the salt for
	 * 3000 round, format the hash and salt, then write the formatted result to
	 * the passwd file
	 */
	@Override
	public void setPassword(String pass) {
		this.password = pass;
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		generateSalt(12);
		hash = hashWithSalt(bytes, salt, 3000);
		writePasswdFile(new PasswordFile(hash, salt));
		ready = true;
	}

	public byte[] getHash() {
		return hash;
	}

	/**
	 * reload the hash and salt from the file
	 */
	@Override
	public void reload() {
		hash = null;
		salt = null;
		loadPasswd();
	}

	/**
	 * calculate the hash, and compare it with the hash stored in the password
	 * file
	 * 
	 * @see HashAuthManager#setPassword(String)
	 */
	@Override
	public boolean verify(String pass) {
		if (!ready) {
			loadPasswd();
		}
		byte[] bytes = pass.getBytes(StandardCharsets.UTF_16);

		byte[] providedHash = hashWithSalt(bytes, salt, 3000);

		if (Arrays.equals(providedHash, hash)) {
			this.password = pass;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getPassword() {
		return password;
	}
}
