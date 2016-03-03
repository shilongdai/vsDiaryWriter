package net.viperfish.utils.compression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Compressors {

	private static Map<String, Class<? extends Compressor>> compressorMap;
	private static Map<String, Compressor> cache;

	static {
		compressorMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		cache = new HashMap<>();
		initCompressors();
	}

	private static void initCompressors() {
		compressorMap.put("GZ", GZipCompressor.class);
		compressorMap.put("BZ2", BZ2Compressor.class);
		compressorMap.put("XZ", XZCompressor.class);
		compressorMap.put("DEFLATE", DEFLATECompressor.class);
		compressorMap.put("None", NullCompressor.class);
	}

	private Compressors() {

	}

	public static Compressor getCompressor(String type) throws FailToInitCompressionException {
		try {
			Compressor result = cache.get(type);
			if (result == null) {
				result = compressorMap.get(type).newInstance();
				cache.put(type, result);
			}

			return result;
		} catch (Throwable e) {
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
