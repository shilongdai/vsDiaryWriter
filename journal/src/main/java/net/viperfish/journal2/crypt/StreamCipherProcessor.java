package net.viperfish.journal2.crypt;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;

public class StreamCipherProcessor implements Processor {

	private StreamCipher cipher;

	public static final String CIPHER_KEYLENGTH = "crypt.streamCipher.keyLength";
	public static final String CIPHER_ALGORITHM = "crypt.streamCipher.alg";

	public StreamCipherProcessor() {
	}

	private StreamCipher initCipher(boolean forEncryption, CryptoInfo keyInfo) {
		try {
			cipher = StreamCipherEncryptors.INSTANCE.getEncryptor(keyInfo.getAlgorithm());
		} catch (NoSuchAlgorithmException e1) {
			throw new CipherException(e1);
		}
		CipherParameters param;
		if (keyInfo.getNounce().length != 0) {
			param = new ParametersWithIV(new KeyParameter(keyInfo.getKey()), keyInfo.getNounce());
		} else {
			param = new KeyParameter(keyInfo.getKey());
		}
		cipher.init(forEncryption, param);
		return cipher;
	}

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, CryptoInfo keyInfo) throws CipherException {
		cipher = initCipher(true, keyInfo);
		Map<String, byte[]> result = new HashMap<>();
		result.put("content", CryptUtils.INSTANCE.transformData(data.get("content"), cipher));
		cipher.reset();
		return result;
	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, CryptoInfo keyInfo) throws CipherException {
		cipher = initCipher(false, keyInfo);
		Map<String, byte[]> result = new HashMap<>();
		result.put("content", CryptUtils.INSTANCE.transformData(data.get("content"), cipher));
		cipher.reset();
		return result;
	}

	@Override
	public String getId() {
		return "streamCipherProcessor";
	}

	@Override
	public CryptoInfoGenerator generator() {
		return new CryptoInfoGenerator() {

			@Override
			public void generate(CryptoInfo info) {
				String algorithm;
				int keyLength;

				if (JournalConfiguration.containsKey(CIPHER_ALGORITHM)) {
					algorithm = JournalConfiguration.getString(CIPHER_ALGORITHM);
				} else {
					algorithm = "XSalsa20";
				}

				if (JournalConfiguration.containsKey(CIPHER_KEYLENGTH)) {
					keyLength = JournalConfiguration.getInt(CIPHER_KEYLENGTH);
				} else {
					keyLength = StreamCipherEncryptors.INSTANCE.getKeySize("XSalsa20");
				}

				byte[] key = CryptUtils.INSTANCE.generateNonce(keyLength / 8);
				byte[] iv = CryptUtils.INSTANCE.generateNonce(StreamCipherEncryptors.INSTANCE.getIVSize(algorithm) / 8);
				info.setKey(key);
				info.setNounce(iv);
				info.setAlgorithm(algorithm);
			}
		};
	}

}
