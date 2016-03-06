package net.viperfish.journal.authProvider;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

public class UnixLikeAuthManager implements AuthenticationManager {

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

	private byte[] ecbEncrypt(byte[] key) {
		ecbCipher.init(true, new KeyParameter(key));
		byte[] result = new byte[ecbCipher.getBlockSize()];
		ecbCipher.processBlock(salt, 0, result, 0);
		for (int i = 0; i < 3000; ++i) {
			ecbCipher.processBlock(result, 0, result, 0);
		}
		return result;
	}

	public void flushPassword() {
		String combo = Base64.encodeBase64String(shadowPassword) + "$" + Base64.encodeBase64String(salt);
		passwdFile.write(combo, StandardCharsets.UTF_8);
	}

	private void generateSalt() {
		salt = new byte[ecbCipher.getBlockSize()];
		rand.nextBytes(salt);
	}

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
		passwdFile.clear();
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
		String combo = passwdFile.read(StandardCharsets.UTF_8);
		String[] parts = combo.split("\\$");
		if (parts.length < 2) {
			return;
		}
		String password = parts[0];
		String salt = parts[1];
		shadowPassword = Base64.decodeBase64(password);
		this.salt = Base64.decodeBase64(salt);

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
