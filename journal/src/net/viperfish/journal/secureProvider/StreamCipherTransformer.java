package net.viperfish.journal.secureProvider;

import java.io.File;
import java.security.SecureRandom;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;
import net.viperfish.journal.streamCipher.StreamCipherEncryptor;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;
import net.viperfish.utils.ByteParameterPair;

final class StreamCipherTransformer extends CompressMacTransformer {

	public static final String ALG_NAME = "net.viperfish.secure.streamCipher";

	private byte[] masterKey;
	private MacDigester macGen;
	private SecureRandom rand;
	private String algName;

	StreamCipherTransformer(File salt) {
		super(salt);
		rand = new SecureRandom();
		algName = Configuration.getString(ALG_NAME);
		if (algName == null) {
			algName = "ChaCha";
		}
		macGen = Macs.getMac("HMAC");
		macGen.setMode("SHA512");
	}

	private byte[] generateSubKey(byte[] feed, int length) {
		byte[] subKey = new byte[length];
		macGen.setKey(masterKey);

		int currentLength = 0;
		while (currentLength != length) {
			byte[] temp = macGen.calculateMac(feed);
			int willAdd = (length - currentLength) > temp.length ? temp.length : length - currentLength;
			System.arraycopy(temp, 0, subKey, currentLength, willAdd);
			currentLength += willAdd;
			feed = temp;
		}
		return subKey;

	}

	@Override
	public void setPassword(String pass) {
		masterKey = this.generateKey(pass);
		byte[] macKey = generateSubKey("Mac Key".getBytes(), getMacKeySize() / 8);
		this.initMac(macKey);
	}

	@Override
	protected String encryptData(byte[] bytes) {
		byte[] randomSource = new byte[16];
		rand.nextBytes(randomSource);

		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize(algName) / 8;
		int ivsize = StreamCipherEncryptors.INSTANCE.getIVSize(algName) / 8;

		byte[] keyCombo = generateSubKey(randomSource, keysize + ivsize);
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

		byte[] keyCombo = generateSubKey(pair.getFirst(), keysize + ivsize);
		byte[] key = new byte[keysize];
		byte[] iv = new byte[ivsize];
		System.arraycopy(keyCombo, 0, key, 0, keysize);
		System.arraycopy(keyCombo, keysize, iv, 0, ivsize);

		StreamCipherEncryptor enc = StreamCipherEncryptors.INSTANCE.getEncryptor(algName);

		return enc.decrypt(pair.getSecond(), key, iv);
	}

}
