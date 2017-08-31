package net.viperfish.journal2.crypt;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

@Component
class AEADProccessor implements Processor {

	private static final String CRYPTOINFO_MAPPING = "aeadProcessor";

	public static final String CONFIG_ENCRYPTION_ALGORITHM = "crypt.encryption.algorithm";
	public static final String CONFIG_ENCRYPTION_KEYLENGTH = "crypt.encryption.keyLength";
	public static final String CONFIG_ENCRYPTION_MODE = "crypt.encryption.mode";

	private AEADBlockCipher cipher;
	private String lastCipher;
	private String lastMode;

	public AEADProccessor() {
		lastCipher = new String();
		lastMode = new String();
	}

	private AEADBlockCipher initCipher(CryptoInfo info) {
		if (info.getAlgorithm().equalsIgnoreCase(lastCipher) && info.getMode().equalsIgnoreCase(lastMode)) {
			return cipher;
		} else {
			cipher = BlockCiphers.useAEADMode(BlockCiphers.getBlockCipherEngine(info.getAlgorithm()), info.getMode());
			this.lastCipher = info.getAlgorithm();
			this.lastMode = info.getMode();
			return cipher;
		}
	}

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException {
		CryptoInfo c = info.get(CRYPTOINFO_MAPPING);
		cipher = initCipher(c);
		int aeadSize;
		if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
			aeadSize = 128;
		} else {
			aeadSize = 16;
		}
		cipher.init(true, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {
			try {
				result.put(e.getKey(), CryptUtils.INSTANCE.transformData(cipher, e.getValue(), new byte[0]));
			} catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
				throw new CipherException(ex);
			}
		}
		return result;

	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException, CompromisedDataException {
		CryptoInfo c = info.get(CRYPTOINFO_MAPPING);
		cipher = initCipher(c);
		int aeadSize;
		if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
			aeadSize = 128;
		} else {
			aeadSize = 16;
		}
		cipher.init(false, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {

			try {
				result.put(e.getKey(), CryptUtils.INSTANCE.transformData(cipher, e.getValue(), new byte[0]));
			} catch (DataLengthException | IllegalStateException e1) {
				throw new CipherException(e1);
			} catch (InvalidCipherTextException e1) {
				throw new CompromisedDataException(e1);
			}

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
			public void generate(Map<String, CryptoInfo> target, Configuration config) {
				String algorithm;
				String mode;
				if (config.containsKey(CONFIG_ENCRYPTION_ALGORITHM)) {
					algorithm = config.getString(CONFIG_ENCRYPTION_ALGORITHM);
				} else {
					algorithm = "AES";
				}
				if (config.containsKey(CONFIG_ENCRYPTION_MODE)) {
					mode = config.getString(CONFIG_ENCRYPTION_MODE);
				} else {
					mode = "GCM";
				}

				SecureRandom rand = new SecureRandom();
				CryptoInfo info = new CryptoInfo();
				info.setAlgorithm(algorithm);
				info.setMode(mode);

				byte[] key = new byte[config.containsKey(CONFIG_ENCRYPTION_KEYLENGTH)
						? config.getInt(CONFIG_ENCRYPTION_KEYLENGTH) / 8
						: BlockCiphers.getKeySize(config.getString(CONFIG_ENCRYPTION_ALGORITHM)) / 8];
				rand.nextBytes(key);
				byte[] iv = CryptUtils.INSTANCE
						.generateNonce(BlockCiphers.getNounceSize(config.getString(CONFIG_ENCRYPTION_MODE)));

				info.setKey(key);
				info.setNounce(iv);

				target.put(CRYPTOINFO_MAPPING, info);
			}
		};
	}

}
