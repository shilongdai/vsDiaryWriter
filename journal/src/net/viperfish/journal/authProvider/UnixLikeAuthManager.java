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
 * operation similar to traditional Unix authentication
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
	 * Encrypt salt(one block) using ECB mode of a block cipher
	 * 
	 * @param key
	 *            the key to encrypt salt with
	 * @return the encrypted salt
	 */
	private byte[] ecbEncrypt(byte[] key) {
		ecbCipher.init(true, new KeyParameter(key));
		byte[] result = new byte[ecbCipher.getBlockSize()];
		ecbCipher.processBlock(salt, 0, result, 0);
		for (int i = 0; i < 3000; ++i) {
			ecbCipher.processBlock(result, 0, result, 0);
		}
		return result;
	}

	/**
	 * writes the encrypted salt and salt to the password file in the format of
	 * encrypted$salt
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

	public UnixLikeAuthManager(File dataDir) {
		ready = false;
		passwdFile = new IOFile(new File(dataDir, "passwd"), new TextIOStreamHandler());
	}

	@Override
	public void clear() {
		try {
			passwdFile.clear();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

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

	@Override
	public void reload() {
		String combo;
		PasswordFile pFile = null;
		try {
			combo = passwdFile.read(StandardCharsets.UTF_8);
			if (combo.length() != 0)
				pFile = PasswordFile.getPasswordData(combo);
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

	@Override
	public String getPassword() {
		return password;
	}

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
