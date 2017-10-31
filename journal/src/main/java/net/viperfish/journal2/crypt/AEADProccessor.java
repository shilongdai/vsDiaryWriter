package net.viperfish.journal2.crypt;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;

public class AEADProccessor implements Processor {

	private static final String CRYPTOINFO_MAPPING = "aeadProcessor";

	public static final String CONFIG_ENCRYPTION_ALGORITHM = "crypt.encryption.algorithm";
	public static final String CONFIG_ENCRYPTION_KEYLENGTH = "crypt.encryption.keyLength";
	public static final String CONFIG_ENCRYPTION_MODE = "crypt.encryption.mode";

	public AEADProccessor() {
	}

	private AEADBlockCipher initCipher(CryptoInfo info) {
		return BlockCiphers.useAEADMode(BlockCiphers.getBlockCipherEngine(info.getAlgorithm()), info.getMode());
	}

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException {
		CryptoInfo c = info.get(CRYPTOINFO_MAPPING);
		AEADBlockCipher cipher = initCipher(c);
		int aeadSize;
		if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
			aeadSize = 128;
		} else {
			aeadSize = 16;
		}
		cipher.init(true, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
		Map<String, byte[]> result = new HashMap<>();
		byte[] content = data.get("content");
		try {
			result.put("content", CryptUtils.INSTANCE.transformData(cipher, content, data.get("subject")));
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
			throw new CipherException(ex);
		}
		return result;

	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException {
		CryptoInfo c = info.get(CRYPTOINFO_MAPPING);
		AEADBlockCipher cipher = initCipher(c);
		int aeadSize;
		if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
			aeadSize = 128;
		} else {
			aeadSize = 16;
		}
		cipher.init(false, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
		Map<String, byte[]> result = new HashMap<>();
		byte[] contentBytes = data.get("content");
		try {
			result.put("content", CryptUtils.INSTANCE.transformData(cipher, contentBytes, data.get("subject")));
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e1) {
			throw new CipherException(e1);
		}

		return result;
	}

	@Override
	public String getId() {
		return "aeadEncryption";
	}

	@Override
	public CryptoInfoGenerator generator() {
		return new CryptoInfoGenerator() {

			@Override
			public void generate(Map<String, CryptoInfo> target) {
				String algorithm;
				String mode;
				if (JournalConfiguration.containsKey(CONFIG_ENCRYPTION_ALGORITHM)) {
					algorithm = JournalConfiguration.getString(CONFIG_ENCRYPTION_ALGORITHM);
				} else {
					algorithm = "AES";
				}
				if (JournalConfiguration.containsKey(CONFIG_ENCRYPTION_MODE)) {
					mode = JournalConfiguration.getString(CONFIG_ENCRYPTION_MODE);
				} else {
					mode = "GCM";
				}

				SecureRandom rand = new SecureRandom();
				CryptoInfo info = new CryptoInfo();
				info.setAlgorithm(algorithm);
				info.setMode(mode);

				byte[] key = new byte[JournalConfiguration.containsKey(CONFIG_ENCRYPTION_KEYLENGTH)
						? JournalConfiguration.getInt(CONFIG_ENCRYPTION_KEYLENGTH) / 8
						: BlockCiphers.getKeySize(JournalConfiguration.getString(CONFIG_ENCRYPTION_ALGORITHM)) / 8];
				rand.nextBytes(key);
				byte[] iv = CryptUtils.INSTANCE.generateNonce(
						BlockCiphers.getNounceSize(JournalConfiguration.getString(CONFIG_ENCRYPTION_MODE)));

				info.setKey(key);
				info.setNounce(iv);

				target.put(CRYPTOINFO_MAPPING, info);
			}
		};
	}

}
