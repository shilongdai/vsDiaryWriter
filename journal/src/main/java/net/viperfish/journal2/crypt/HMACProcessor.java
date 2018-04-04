package net.viperfish.journal2.crypt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;

public class HMACProcessor implements Processor {

	public final static String MAC_ALGORITHM = "mac.hmac.alg";
	public final static String MAC_SIZE = "mac.hmac.keySize";

	private HMac mac;
	private String lastAlg;

	public HMACProcessor() {
	}

	private HMac initMac(CryptoInfo info) {
		if (lastAlg == null || !info.getAlgorithm().equalsIgnoreCase(lastAlg)) {
			try {
				Digest digest = Digesters.getDigester(info.getAlgorithm());
				digest.reset();
				mac = new HMac(digest);
				lastAlg = info.getAlgorithm();
			} catch (NoSuchAlgorithmException ex) {
				throw new CipherException(ex);
			}
		}
		mac.reset();
		mac.init(new KeyParameter(info.getKey()));
		return mac;
	}

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, CryptoInfo c) throws CipherException {
		mac = initMac(c);
		byte[] contentByte = data.get("content");
		byte[] subjectByte = data.get("subject");
		byte[] combined = new byte[contentByte.length + subjectByte.length];
		System.arraycopy(subjectByte, 0, combined, 0, subjectByte.length);
		System.arraycopy(contentByte, 0, combined, subjectByte.length, combined.length);
		Map<String, byte[]> result = new HashMap<>();
		byte[] macResult = CryptUtils.INSTANCE.calculateMac(combined, this.mac);
		byte[] output = new byte[contentByte.length + macResult.length];
		System.arraycopy(contentByte, 0, output, 0, contentByte.length);
		System.arraycopy(macResult, 0, output, contentByte.length, macResult.length);
		result.put("content", output);
		return result;
	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, CryptoInfo c) throws CipherException {
		mac = initMac(c);
		byte[] contentByte = data.get("content");
		Map<String, byte[]> result = new HashMap<>();
		byte[] merged = contentByte;
		byte[] macBytes = new byte[mac.getMacSize()];
		System.arraycopy(merged, merged.length - mac.getMacSize(), macBytes, 0, mac.getMacSize());
		byte[] dataSection = new byte[merged.length - macBytes.length];
		System.arraycopy(merged, 0, dataSection, 0, dataSection.length);
		byte[] subjectByte = data.get("subject");
		byte[] combined = new byte[dataSection.length + subjectByte.length];
		System.arraycopy(subjectByte, 0, combined, 0, subjectByte.length);
		System.arraycopy(dataSection, 0, combined, subjectByte.length, combined.length);
		byte[] calculatedMac = CryptUtils.INSTANCE.calculateMac(combined, mac);
		if (!Arrays.equals(macBytes, calculatedMac)) {
			throw new CipherException();
		}

		result.put("content", dataSection);

		return result;
	}

	@Override
	public String getId() {
		return "hmac";
	}

	@Override
	public CryptoInfoGenerator generator() {
		return new CryptoInfoGenerator() {

			@Override
			public void generate(CryptoInfo info) {
				String alg;
				if (JournalConfiguration.containsKey(MAC_ALGORITHM)) {
					alg = JournalConfiguration.getString(MAC_ALGORITHM);
				} else {
					alg = "SHA256";
				}
				int macSize;
				if (JournalConfiguration.containsKey(MAC_SIZE)) {
					macSize = JournalConfiguration.getInt(MAC_SIZE);
				} else {
					macSize = 32;
				}
				info.setAlgorithm(alg);
				info.setMode("HMAC");
				SecureRandom rand = new SecureRandom();
				byte[] key = new byte[macSize];
				rand.nextBytes(key);
				info.setKey(key);
			}
		};
	}

}
