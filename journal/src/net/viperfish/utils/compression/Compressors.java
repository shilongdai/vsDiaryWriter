package net.viperfish.utils.compression;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Compressors {

	private static Map<String, Class<? extends Compressor>> compressorMap;

	static {
		compressorMap = new TreeMap<>();
		initCompressors();
	}

	private static void initCompressors() {
		compressorMap.put("gz", GZipCompressor.class);
	}

	private Compressors() {

	}

	public static Compressor getCompressor(String type) throws FailToInitCompressionException {
		try {
			return compressorMap.get(type).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new FailToInitCompressionException(type + " cannot be initialized");
		}
	}

	public static String[] getCompressors() {
		List<String> result = new LinkedList<>();
		for (Entry<String, Class<? extends Compressor>> i : compressorMap.entrySet()) {
			result.add(i.getKey());
		}
		return result.toArray(new String[0]);
	}
}
