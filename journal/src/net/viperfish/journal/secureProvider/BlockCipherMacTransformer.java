package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;
import net.viperfish.journal.secureAlgs.BlockCipherEncryptor;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;
import net.viperfish.utils.ByteParameterPair;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * a journal transformer that uses a compression, a block cipher, and a mac
 * algorithm to cipher an entry
 * 
 * @author sdai
 *
 */
final class BlockCipherMacTransformer extends CompressMacTransformer {

	BlockCipherMacTransformer(File salt) {
		super(salt);

		enc = BlockCiphers.getEncryptor(Configuration.getString(ENCRYPTION_ALG_NAME),
				Configuration.getString(ENCRYPTION_MODE), Configuration.getString(ENCRYPTION_PADDING));
		rand = new SecureRandom();

		String hkdf = Configuration.getString(HKDF_ALGORITHM);
		if (hkdf == null || hkdf.length() == 0) {
			hkdf = "SHA256";
		}
		this.hkdf = new HKDFBytesGenerator(Digesters.getDigester(hkdf));

		salts = new IOFile(new File(salt.getParentFile(), "additionalSalts"), new TextIOStreamHandler());
	}

	public static final String ENCRYPTION_ALG_NAME = "viperfish.secure.encrytion.algorithm";
	public static final String ENCRYPTION_MODE = "viperfish.secure.encryption.mode";
	public static final String ENCRYPTION_PADDING = "viperfish.secure.encryption.padding";
	public static final String HKDF_ALGORITHM = "viperfish.secure.encryption.hkdf";

	private byte[] key;
	private BlockCipherEncryptor enc;
	private byte[] cryptKey;
	private SecureRandom rand;

	private IOFile salts;
	private ByteParameterPair saltPair;
	private HKDFBytesGenerator hkdf;

	/**
	 * derive the key from the password
	 * 
	 * @throws FailToSyncCipherDataException
	 */
	@Override
	public void setPassword(String string) throws FailToSyncCipherDataException {
		try {
			String saltRaw = salts.read(StandardCharsets.US_ASCII);
			if (saltRaw.length() > 0) {
				saltPair = ByteParameterPair.valueOf(saltRaw);
			} else {
				byte[] encSalt = new byte[8];
				byte[] macSalt = new byte[8];
				rand.nextBytes(encSalt);
				rand.nextBytes(macSalt);
				saltPair = new ByteParameterPair(encSalt, macSalt);
				salts.write(saltPair.toString(), StandardCharsets.US_ASCII);
			}
		} catch (IOException e) {
			FailToSyncCipherDataException fc = new FailToSyncCipherDataException(
					"Cannot load encryption and mac salt from file");
			fc.initCause(e);
			throw fc;
		}

		this.key = generateKey(string);

		hkdf.init(new HKDFParameters(key, saltPair.getFirst(), "Encryption Key".getBytes()));
		cryptKey = new byte[enc.getKeySize() / 8];
		hkdf.generateBytes(cryptKey, 0, cryptKey.length);

		byte[] macKey = new byte[getMacKeySize() / 8];
		hkdf.init(new HKDFParameters(key, saltPair.getSecond(), "Mac Key".getBytes()));
		hkdf.generateBytes(macKey, 0, macKey.length);

		initMac(macKey);
	}

	@Override
	protected String encryptData(byte[] bytes) throws CipherException {
		byte[] iv = new byte[enc.getBlockSize()];
		rand.nextBytes(iv);
		byte[] cipher = enc.encrypt(bytes, cryptKey, iv);
		ByteParameterPair pair = new ByteParameterPair(cipher, iv);
		return pair.toString();
	}

	@Override
	protected byte[] decryptData(String data) throws CipherException {
		ByteParameterPair pair = ByteParameterPair.valueOf(data);
		return enc.decrypt(pair.getFirst(), cryptKey, pair.getSecond());
	}

}
