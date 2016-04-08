package net.viperfish.journal.secureProvider;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.journal.streamCipher.StreamCipherEncryptor;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;

final class StreamCipherTransformer implements JournalTransformer {

	private StreamCipherEncryptor enc;
	private byte[] masterKey;
	private PBKDF2KeyGenerator generator;
	private MacDigester macGen;

	StreamCipherTransformer() {
		enc = StreamCipherEncryptors.INSTANCE.getEncryptor("HC256");
		generator = new BCPCKDF2Generator();
		generator.setDigest("SHA256");
		generator.setIteration(64000);
		generator.setSalt(new byte[] { 44, 45, 23, 64, 98, 36, 74, 53 });
		macGen = Macs.getMac("HMAC");
		macGen.setMode("SHA512");
	}

	private byte[] generateSubKey(Journal feed, int length) {
		byte[] subKey = new byte[length];
		macGen.setKey(masterKey);

		String date = feed.getDate().toString();

		String combo = date;

		byte[] macContent = combo.getBytes(StandardCharsets.UTF_16);

		int currentLength = 0;
		while (currentLength != length) {
			byte[] temp = macGen.calculateMac(macContent);
			int willAdd = (length - currentLength) > temp.length ? temp.length : length - currentLength;
			System.arraycopy(temp, 0, subKey, currentLength, willAdd);
			currentLength += willAdd;
		}
		return subKey;

	}

	@Override
	public Journal encryptJournal(Journal j) {
		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize("HC256");
		int ivSize = StreamCipherEncryptors.INSTANCE.getIVSize("HC256");
		keysize /= 8;
		ivSize /= 8;
		byte[] combo = generateSubKey(j, ivSize + keysize);
		byte[] key = new byte[keysize];
		System.arraycopy(combo, 0, key, 0, keysize);
		byte[] iv = new byte[ivSize];
		System.arraycopy(combo, keysize, iv, 0, ivSize);

		byte[] encryptedSubject = enc.encrypt(j.getSubject().getBytes(StandardCharsets.UTF_16), key, iv);
		byte[] encryptedContent = enc.encrypt(j.getContent().getBytes(StandardCharsets.UTF_16), key, iv);

		Journal result = new Journal(j);
		result.setSubject(Base64.encodeBase64String(encryptedSubject));
		result.setContent(Base64.encodeBase64String(encryptedContent));
		return result;
	}

	@Override
	public Journal decryptJournal(Journal j) {
		int keysize = StreamCipherEncryptors.INSTANCE.getKeySize("HC256");
		int ivSize = StreamCipherEncryptors.INSTANCE.getIVSize("HC256");

		keysize /= 8;
		ivSize /= 8;
		byte[] combo = generateSubKey(j, ivSize + keysize);
		byte[] key = new byte[keysize];
		System.arraycopy(combo, 0, key, 0, keysize);
		byte[] iv = new byte[ivSize];
		System.arraycopy(combo, keysize, iv, 0, ivSize);

		byte[] cipheredSubject = Base64.decodeBase64(j.getSubject());
		byte[] cipheredContent = Base64.decodeBase64(j.getContent());

		String subject = new String(enc.decrypt(cipheredSubject, key, iv), StandardCharsets.UTF_16);
		String content = new String(enc.decrypt(cipheredContent, key, iv), StandardCharsets.UTF_16);

		Journal result = new Journal(j);
		result.setSubject(subject);
		result.setContent(content);
		return result;
	}

	@Override
	public void setPassword(String pass) {
		masterKey = generator.generate(pass, generator.getDigestSize());
	}

}
