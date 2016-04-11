package net.viperfish.journal.authProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * An Authentication Manager using encryption algorithm as a hash function with
 * operation similar to traditional Unix authentication. It uses a selected
 * blockcipher and encrypt a randomly generated salt using a key derived from
 * the password to generate the "hash". The hash and the salt would be stored as
 * {@link PasswordFile}.
 * 
 * <p>
 * This class is NOT thread safe
 * </p>
 * 
 * @author sdai
 *
 */
final class UnixLikeAuthManager implements AuthenticationManager {

	public static final String ENCRYPTION_ALG = "viperfish.unixAuth.cipher";
	public static final String KDF_HASH = "viperfish.unixAuth.kdf";

	private BlockCipher ecbCipher;
	private PBKDF2KeyGenerator generator;
	private SecureRandom rand;
	private IOFile passwdFile;
	private byte[] shadowPassword;
	private String password;
	private byte[] salt;
	private int keySize;
	private boolean ready;

	/**
	 * encrypt salt(one block) using ECB mode of a block cipher.
	 * 
	 * @param key
	 *            the key to encrypt salt with
	 * @return the encrypted salt
	 */
	private byte[] ecbEncrypt(byte[] key) {
		ecbCipher.init(true, new KeyParameter(key));
		byte[] result = new byte[ecbCipher.getBlockSize()];
		ecbCipher.processBlock(salt, 0, result, 0);
		for (int i = 0; i < 30000; ++i) {
			ecbCipher.processBlock(result, 0, result, 0);
		}
		return result;
	}

	/**
	 * writes the encrypted salt and salt to the password file
	 */
	public void flushPassword() {
		PasswordFile pFile = new PasswordFile(shadowPassword, salt);
		try {
			passwdFile.write(pFile.toString(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			FailToStoreCredentialException f = new FailToStoreCredentialException(
					"Cannot store data to password file:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * generate salt with a secure random number generator
	 */
	private void generateSalt() {
		salt = new byte[ecbCipher.getBlockSize()];
		rand.nextBytes(salt);
	}

	/**
	 * initialize the block cipher, kdf, and random number generator
	 */
	private void initAlg() {
		ecbCipher = BlockCiphers.getBlockCipherEngine(Configuration.getString(ENCRYPTION_ALG));
		generator = new BCPCKDF2Generator();
		generator.setDigest(Configuration.getString(KDF_HASH));
		generator.setIteration(3000);
		rand = new SecureRandom();
		keySize = BlockCiphers.getKeySize(Configuration.getString(ENCRYPTION_ALG));
		ready = true;
	}

	/**
	 * creates a new UnixLikeAuthManager with the directory to store password.
	 * 
	 * This constructor creates a UnixLikeAuthManager that will store the
	 * password in dataDir/passwd.
	 * 
	 * @param dataDir
	 *            the directory of the password file
	 */
	public UnixLikeAuthManager(File dataDir) {
		ready = false;
		passwdFile = new IOFile(new File(dataDir, "passwd"), new TextIOStreamHandler());
	}

	/**
	 * overwrites the password file with an empty string
	 * 
	 * This method writes "" to the password file and resets internal data. If
	 * an exception occurs, it will be wrapped in a {@link RuntimeException} and
	 * thrown.
	 * 
	 * @throws IOException
	 *             if failed to clear the passwd file
	 */
	@Override
	public void clear() {
		try {
			this.salt = null;
			this.shadowPassword = null;
			this.password = null;
			passwdFile.clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * generates a new hash and stores the password
	 * 
	 * This method initializes encryption algorithms if they are not ready,
	 * generates a new salt and hash, and stores the credential to the password
	 * file in the format of {@link PasswordFile}. If the function fails, the
	 * object would be in an unusable state until a call to reload or
	 * setPassword succeeds. If an exception occurs, it would be wrapped by a
	 * {@link RuntimeException} and thrown.
	 * 
	 * @param pass
	 *            the password of the user
	 * @throws FailToStoreCredentialException
	 *             if failed to flush password
	 */
	@Override
	public void setPassword(String pass) {
		if (!ready) {
			initAlg();
		}
		generateSalt();
		generator.setSalt(salt);
		byte[] key = generator.generate(pass, keySize);
		this.shadowPassword = ecbEncrypt(key);
		this.password = pass;
		flushPassword();
	}

	/**
	 * reloads the password file
	 * 
	 * This method reloads the content of the password file into memory. If it
	 * succeeds and the password file contains the credential, it would leave
	 * the object in a state that is ready to verify passwords. If it fails, the
	 * object would be unusable until a call to reload or setPassword succeeds.
	 * If an exception occurs, it would be wrapped in a {@link RuntimeException}
	 * and thrown.
	 * 
	 * @throws FailToLoadCredentialException
	 *             if failed to read the file or parse the content
	 */
	@Override
	public void reload() {
		String combo;
		PasswordFile pFile = null;
		try {
			combo = passwdFile.read(StandardCharsets.UTF_8);
			if (combo.length() != 0)
				pFile = PasswordFile.valueOf(combo);
			else
				return;
		} catch (IOException | IllegalArgumentException e) {
			FailToLoadCredentialException f = new FailToLoadCredentialException(
					"Cannot load password file:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(f);
		}
		this.shadowPassword = pFile.getCredentialInfo();
		this.salt = pFile.getSalt();

	}

	/**
	 * get the stored password
	 * 
	 * This method returns the plain text password specified by the user. Until
	 * a {@link UnixLikeAuthManager#setPassword(String)} or a
	 * {@link UnixLikeAuthManager#verify(String)} is called, this returns null.
	 * 
	 * @return the stored password
	 */
	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * verifies if a plain text password is correct
	 * 
	 * This method verifies if a password matches the one set by the user in the
	 * previous {@link HashAuthManager#setPassword(String)}.
	 * 
	 * @return true if correct and false incorrect
	 */
	@Override
	public boolean verify(String string) {
		if (!ready) {
			initAlg();
		}
		generator.setSalt(salt);
		byte[] key = generator.generate(string, keySize);
		byte[] cryptKey = ecbEncrypt(key);
		if (Arrays.equals(cryptKey, shadowPassword)) {
			this.password = string;
			return true;
		}
		return false;
	}

}
