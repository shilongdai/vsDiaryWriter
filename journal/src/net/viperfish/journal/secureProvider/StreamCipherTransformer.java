package net.viperfish.journal.secureProvider;

import java.io.File;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base64;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;
import net.viperfish.journal.streamCipher.StreamCipherEncryptor;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;

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

		String randString = Base64.encodeBase64String(randomSource);
		String encryptedString = Base64.encodeBase64String(ecrypted);
		return randString + "&" + encryptedString;
	}

	@Override
	protected byte[] decryptData(String cData) {
		String[] segments = cData.split("&");
		if (segments.length != 2) {
			throw new IllegalArgumentException("Expected 2 segments, got:" + segments.length);
		}

		byte[] randomSource = Base64.decodeBase64(segments[0]);
		byte[] ciphered = Base64.decodeBase64(segments[1]);

		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize(algName) / 8;
		int ivsize = StreamCipherEncryptors.INSTANCE.getIVSize(algName) / 8;

		byte[] keyCombo = generateSubKey(randomSource, keysize + ivsize);
		byte[] key = new byte[keysize];
		byte[] iv = new byte[ivsize];
		System.arraycopy(keyCombo, 0, key, 0, keysize);
		System.arraycopy(keyCombo, keysize, iv, 0, ivsize);

		StreamCipherEncryptor enc = StreamCipherEncryptors.INSTANCE.getEncryptor(algName);

		return enc.decrypt(ciphered, key, iv);
	}

}
