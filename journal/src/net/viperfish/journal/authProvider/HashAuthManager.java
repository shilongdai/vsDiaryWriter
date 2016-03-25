package net.viperfish.journal.authProvider;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;
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
class HashAuthManager implements AuthenticationManager {

	public static final String HASH_ALG = "viperfish.auth.hash";

	private Digester dig;
	private IOFile passwdFile;
	private String password;
	private byte[] hash;
	private byte[] salt;
	private SecureRandom rand;
	private boolean ready;

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
	 * encode the hash and salt into Base64 strings, and combine them so that
	 * it's hash$salt
	 * 
	 * @return
	 */
	private String hashAuthFormat() {
		String hash64 = Base64.encodeBase64String(hash);
		String salt64 = Base64.encodeBase64String(salt);
		String result = hash64 + "$" + salt64;
		return result;
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
	 *            the formatted hash and salt
	 */
	private void writePasswdFile(String formatted) {
		passwdFile.write(formatted, StandardCharsets.UTF_8);
	}

	/**
	 * read the hash and salt from the password file, the content read are UTF-8
	 * characters
	 * 
	 * @return the combination of hash and salt
	 */
	private String readPasswdFile() {
		return passwdFile.read(StandardCharsets.UTF_8);
	}

	/**
	 * load the hash and salt from file into memory
	 */
	private void loadPasswd() {
		String formatedPasswd = readPasswdFile();
		String[] parts = formatedPasswd.split("\\$");
		if (parts.length < 2) {
			return;
		}
		String hash = parts[0];
		String salt = parts[1];
		this.hash = Base64.decodeBase64(hash);
		this.salt = Base64.decodeBase64(salt);
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
		passwdFile.clear();

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
		String formatted = hashAuthFormat();
		writePasswdFile(formatted);
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
