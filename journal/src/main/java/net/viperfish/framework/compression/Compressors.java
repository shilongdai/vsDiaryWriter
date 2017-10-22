package net.viperfish.framework.compression;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * static factory for compressors
 *
 * @author sdai
 *
 */
public final class Compressors {

    private static Map<String, Compressor> cache;

    static {
        cache = new HashMap<>();
        initCompressors();
    }

    private static void initCompressors() {
    }

    private Compressors() {

    }

    /**
     * get a compressor of specified type
     *
     * @param type the type of compressor
     * @return the compressor
     * @throws NoSuchAlgorithmException if the compressor is not found
     */
    public static Compressor getCompressor(String type) throws NoSuchAlgorithmException {
        Compressor result = cache.get(type);
        if (result == null) {
            result = createCompressor(type);
            cache.put(type, result);
        }
        return result;
    }

    /**
     * get supported compressors
     *
     * @return supported compressors
     */
    public static String[] getCompressors() {
        return new String[]{"GZ", "BZ2", "XZ", "DEFLATE", "NONE"};
    }

    private static Compressor createCompressor(String type) throws NoSuchAlgorithmException {
        switch (type) {
            case "GZ": {
                return new GZipCompressor();
            }
            case "BZ2": {
                return new BZ2Compressor();
            }
            case "XZ": {
                return new XZCompressor();
            }
            case "DEFLATE": {
                return new DEFLATECompressor();
            }
            case "None": {
                return new NullCompressor();
            }
            default: {
                throw new NoSuchAlgorithmException(type);
            }
        }
    }
}
