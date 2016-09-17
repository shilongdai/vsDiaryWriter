package net.viperfish.journal2.crypt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Component;

import net.viperfish.framework.compression.Compressor;
import net.viperfish.framework.compression.Compressors;
import net.viperfish.framework.compression.FailToInitCompressionException;
import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

@Component
class CompressionProccessor implements Processor {

	public static final String CONFIG_COMPRESSION = "compressor.algorithm";

	private static final String CRYPTOINFO_MAPPING_KEY = "compressionProcessor";

	private Compressor compressor;

	@Override
	public Map<String, byte[]> doProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException {
		try {
			compressor = Compressors.getCompressor(info.get(CRYPTOINFO_MAPPING_KEY).getAlgorithm());
		} catch (FailToInitCompressionException e) {
			CipherException e1 = new CipherException(e);
			throw e1;
		}

		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {
			result.put(e.getKey(), compressor.compress(e.getValue()));
		}
		return result;
	}

	@Override
	public Map<String, byte[]> undoProccess(Map<String, byte[]> data, Map<String, CryptoInfo> info)
			throws CipherException, CompromisedDataException {
		try {
			compressor = Compressors.getCompressor(info.get(CRYPTOINFO_MAPPING_KEY).getAlgorithm());
		} catch (FailToInitCompressionException e) {
			CipherException e1 = new CipherException(e);
			throw e1;
		}

		Map<String, byte[]> result = new HashMap<>();
		for (Entry<String, byte[]> e : data.entrySet()) {
			result.put(e.getKey(), compressor.deflate(e.getValue()));
		}
		return result;
	}

	@Override
	public String getId() {
		return "compression";
	}

	@Override
	public CryptoInfoGenerator generator() {
		return new CryptoInfoGenerator() {

			@Override
			public void generate(Map<String, CryptoInfo> target, Configuration config) {
				String algorithm;
				if (config.containsKey(CONFIG_COMPRESSION)) {
					algorithm = config.getString(CONFIG_COMPRESSION);
				} else {
					algorithm = "GZ";
				}
				String compressionAlg = config.getString(algorithm);
				CryptoInfo info = new CryptoInfo();
				info.setAlgorithm(compressionAlg);
				target.put(CRYPTOINFO_MAPPING_KEY, info);
			}
		};
	}

}
