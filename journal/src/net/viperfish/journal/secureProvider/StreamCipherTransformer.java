package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;

import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;

import net.viperfish.framework.ByteParameterPair;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;
import net.viperfish.journal.secureAlgs.Digesters;
import net.viperfish.journal.streamCipher.StreamCipherEncryptor;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;

final class StreamCipherTransformer extends CompressMacTransformer {

	public static final String ALG_NAME = "net.viperfish.secure.streamCipher";
	public static final String HKDF_ALG = "net.viperfish.secure.streamCipher.hkdf";

	private byte[] masterKey;
	private SecureRandom rand;
	private String algName;
	private HKDFBytesGenerator hkdf;
	private File additSalt;
	private ByteParameterPair salts;

	StreamCipherTransformer(File salt) {
		super(salt);
		additSalt = new File(salt.getParent(), "streamCipherSalt");
		rand = new SecureRandom();
		algName = Configuration.getString(ALG_NAME);
		if (algName == null) {
			algName = "ChaCha";
		}
		String hkdf = Configuration.getString(HKDF_ALG);
		if (hkdf == null) {
			hkdf = "SHA512";
		}
		this.hkdf = new HKDFBytesGenerator(Digesters.getDigester(hkdf));
	}

	@Override
	public void setPassword(String pass) throws FailToSyncCipherDataException {
		if (salts == null) {
			byte[] macSalt = new byte[8];
			byte[] encSalt = new byte[8]; // left blank intentionally
			rand.nextBytes(macSalt);
			salts = new ByteParameterPair(encSalt, macSalt);

			try {
				Files.write(additSalt.toPath(), salts.toString().getBytes(StandardCharsets.US_ASCII),
						StandardOpenOption.WRITE, StandardOpenOption.CREATE);
			} catch (IOException e) {
				FailToSyncCipherDataException fc = new FailToSyncCipherDataException(
						"Cannot write salt to " + additSalt.getAbsolutePath() + " message:" + e.getMessage());
				fc.initCause(e);
				throw fc;
			}
		}

		masterKey = this.generateKey(pass);
		hkdf.init(new HKDFParameters(masterKey, salts.getSecond(), "Mac Key".getBytes()));
		byte[] macKey = new byte[this.getMacKeySize() / 8];
		hkdf.generateBytes(macKey, 0, macKey.length);
		this.initMac(macKey);

	}

	@Override
	protected String encryptData(byte[] bytes) {
		byte[] randomSource = new byte[16];
		rand.nextBytes(randomSource);

		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize(algName) / 8;
		int ivsize = StreamCipherEncryptors.INSTANCE.getIVSize(algName) / 8;

		byte[] keyCombo = new byte[keysize + ivsize];

		hkdf.init(new HKDFParameters(masterKey, randomSource, "Encryption Key".getBytes()));
		hkdf.generateBytes(keyCombo, 0, keyCombo.length);

		byte[] key = new byte[keysize];
		byte[] iv = new byte[ivsize];

		System.arraycopy(keyCombo, 0, key, 0, keysize);
		System.arraycopy(keyCombo, keysize, iv, 0, ivsize);

		StreamCipherEncryptor enc = StreamCipherEncryptors.INSTANCE.getEncryptor(algName);

		byte[] ecrypted = enc.encrypt(bytes, key, iv);

		ByteParameterPair pair = new ByteParameterPair(randomSource, ecrypted);

		return pair.toString();
	}

	@Override
	protected byte[] decryptData(String cData) {
		ByteParameterPair pair = ByteParameterPair.valueOf(cData);

		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize(algName) / 8;
		int ivsize = StreamCipherEncryptors.INSTANCE.getIVSize(algName) / 8;

		byte[] keyCombo = new byte[keysize + ivsize];

		hkdf.init(new HKDFParameters(masterKey, pair.getFirst(), "Encryption Key".getBytes()));
		hkdf.generateBytes(keyCombo, 0, keyCombo.length);

		byte[] key = new byte[keysize];
		byte[] iv = new byte[ivsize];
		System.arraycopy(keyCombo, 0, key, 0, keysize);
		System.arraycopy(keyCombo, keysize, iv, 0, ivsize);

		StreamCipherEncryptor enc = StreamCipherEncryptors.INSTANCE.getEncryptor(algName);

		return enc.decrypt(pair.getSecond(), key, iv);
	}

	void loadSalt() {
		if (additSalt.exists()) {
			try {
				byte[] raw = Files.readAllBytes(additSalt.toPath());
				String encoded = new String(raw, StandardCharsets.US_ASCII);
				if (encoded.length() != 0)
					this.salts = ByteParameterPair.valueOf(encoded);
			} catch (IOException e) {
				FailToSyncCipherDataException fc = new FailToSyncCipherDataException(
						"Cannot load salt from:" + additSalt.getAbsolutePath() + " message:" + e.getMessage());
				fc.initCause(e);
				throw new RuntimeException(fc);
			}
		}
	}

}
