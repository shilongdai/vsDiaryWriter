package net.viperfish.journal2.crypt;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

@Component
class HMACProcessor implements Processor {

	final static String MAC_ALGORITHM = "mac.hmac.alg";
	final static String MAC_SIZE = "mac.hmac.keySize";

	private HMac mac;
	private String lastAlg;

	public HMACProcessor() {
	}

	private HMac initMac(CryptoInfo info) {
		if (lastAlg == null || !info.getAlgorithm().equalsIgnoreCase(lastAlg)) {
			Digest digest = Digesters.getDigester(info.getAlgorithm());
			digest.reset();
			mac = new HMac(digest);
			lastAlg = info.getAlgorithm();
		}
		mac.reset();
		mac.init(new KeyParameter(info.getKey()));
		return mac;
	}

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException {
		CryptoInfo c = info.get("hmac");
		mac = initMac(c);
		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {
			byte[] mac = CryptUtils.INSTANCE.calculateMac(e.getValue(), this.mac);
			byte[] output = new byte[e.getValue().length + mac.length];
			System.arraycopy(e.getValue(), 0, output, 0, e.getValue().length);
			System.arraycopy(mac, 0, output, e.getValue().length, mac.length);
			result.put(e.getKey(), output);
		}
		return result;
	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException, CompromisedDataException {
		CryptoInfo c = info.get("hmac");
		mac = initMac(c);
		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {
			byte[] merged = e.getValue();
			byte[] macBytes = new byte[mac.getMacSize()];
			System.arraycopy(merged, merged.length - mac.getMacSize(), macBytes, 0, mac.getMacSize());
			byte[] dataSection = new byte[merged.length - macBytes.length];
			System.arraycopy(merged, 0, dataSection, 0, dataSection.length);

			byte[] calculatedMac = CryptUtils.INSTANCE.calculateMac(dataSection, mac);
			if (!Arrays.equals(macBytes, calculatedMac)) {
				throw new CompromisedDataException();
			}

			result.put(e.getKey(), dataSection);
		}
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
			public void generate(Map<String, CryptoInfo> target, Configuration config) {
				CryptoInfo info = new CryptoInfo();
				info.setAlgorithm(config.getString(MAC_ALGORITHM));
				info.setMode("HMAC");
				SecureRandom rand = new SecureRandom();
				byte[] key = new byte[config.getInt(MAC_SIZE)];
				rand.nextBytes(key);
				info.setKey(key);
				target.put("hmac", info);
			}
		};
	}

}
