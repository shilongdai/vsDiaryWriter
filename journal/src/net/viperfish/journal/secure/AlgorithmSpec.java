package net.viperfish.journal.secure;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.reflections.Reflections;

public class AlgorithmSpec {
	private static int DES_KEY_SIZE = 64;
	private static int AES_KEY_SIZE = 256;
	private static int DESEDE_KEY_SIZE = 192;
	private static int CAST5_KEY_SIZE = 128;
	private static int CAST6_KEY_SIZE = 256;
	private static int DEFAULT = 256;
	private static int BLOWFISH_KEY_SIZE = 448;
	private static int CAMELLIA_KEY_SIZE = 256;
	private static int GOST28147_KEY_SIZE = 256;
	private static int IDEA_KEY_SIZE = 128;
	private static int NOEKEON_KEY_SIZE = 128;
	private static int RC2_KEY_SIZE = 1024;
	private static int RC5_KEYSIZE = 128;
	private static int RC6_KEY_SIZE = 256;
	private static int SEED_KEY_SIZE = 128;
	private static int SHACAL2_KEY_SIZE = 512;
	private static int SERPENT_KEY_SIZE = 256;
	private static int SKIPJACK_KEY_SIZE = 128;
	private static int TEA_KEY_SIZE = 128;
	private static int TWOFISH_KEY_SIZE = 256;
	private static int XTEA_KEY_SIZE = 128;
	private static int RC4_KEY_SIZE = 2048;
	private static int HC128_KEY_SIZE = 128;
	private static int HC256_KEY_SIZE = 128;
	private static int CHACHA_KEY_SIZE = 256;
	private static int SALSA20_KEY_SIZE = 256;
	private static int XSAlSA20_KEY_SIZE = 256;
	private static int ISAAC_KEY_SIZE = 8192;
	private static int VMPC_KEY_SIZE = 6144;
	private static int GRAINV1_KEY_SIZE = 80;
	private static int GRAIN128_KEY_SIZE = 128;

	private static Map<String, Class<? extends BlockCipher>> blockCipherEngines;
	private static Map<String, Class<? extends BlockCipher>> blockCipherMode;
	private static Map<String, Class<? extends BlockCipherPadding>> blockCipherPadding;
	private static Map<String, Class<? extends Digest>> digesters;

	static {
		blockCipherEngines = new TreeMap<String, Class<? extends BlockCipher>>(
				String.CASE_INSENSITIVE_ORDER);
		blockCipherMode = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		blockCipherPadding = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		digesters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		initBlockCipherEngines();
		initBlockCipherModes();
		initBlockCipherPaddings();
		initDigesters();
	}

	private static void initDigesters() {
		digesters.put("Blake2b", Blake2bDigest.class);
		digesters.put("GOST3411", GOST3411Digest.class);
		digesters.put("Keccak", KeccakDigest.class);
		digesters.put("MD2", MD2Digest.class);
		digesters.put("MD4", MD4Digest.class);
		digesters.put("MD5", MD5Digest.class);
		digesters.put("Null", NullDigest.class);
		digesters.put("RIPEMD128", RIPEMD128Digest.class);
		digesters.put("RIPEMD160", RIPEMD160Digest.class);
		digesters.put("RIPEMD256", RIPEMD256Digest.class);
		digesters.put("RIPEMD320", RIPEMD320Digest.class);
		digesters.put("SHA1", SHA1Digest.class);
		digesters.put("SHA224", SHA224Digest.class);
		digesters.put("SHA256", SHA256Digest.class);
		digesters.put("SHA384", SHA384Digest.class);
		digesters.put("SHA3", SHA3Digest.class);
		digesters.put("SHA512", SHA512Digest.class);
		digesters.put("SHA512t", SHA512tDigest.class);
		digesters.put("SHAKE", SHAKEDigest.class);
		digesters.put("SM3", SM3Digest.class);
		digesters.put("Tiger", TigerDigest.class);
		digesters.put("Whirlpool", WhirlpoolDigest.class);
	}

	private static void initBlockCipherEngines() {
		Reflections scanner = new Reflections("org.bouncycastle.crypto.engines");
		Set<Class<? extends BlockCipher>> implementers = scanner
				.getSubTypesOf(BlockCipher.class);
		for (Class<? extends BlockCipher> i : implementers) {
			if (i.getSimpleName().contains("Engine")) {
				if (i.getSimpleName().contains("Fast")
						|| i.getSimpleName().contains("Light")
						|| i.getSimpleName().contains("Wrap")) {
					continue;
				}
				try {
					blockCipherEngines.put(i.newInstance().getAlgorithmName(),
							i);
				} catch (InstantiationException | IllegalAccessException e) {
					continue;
				}
			}
		}
	}

	private static void initBlockCipherModes() {
		Reflections scanner = new Reflections("org.bouncycastle.crypto.modes");
		Set<Class<? extends BlockCipher>> blockCipherModes = scanner
				.getSubTypesOf(BlockCipher.class);
		for (Class<? extends BlockCipher> i : blockCipherModes) {
			String name = i.getSimpleName();
			name = name.substring(0, name.lastIndexOf("BlockCipher"));
			blockCipherMode.put(name, i);
		}
		blockCipherMode.put("CFB", CFBBlockCipher.class);
		blockCipherMode.put("OFB", OFBBlockCipher.class);
		blockCipherMode.put("CTR", SICBlockCipher.class);
	}

	private static void initBlockCipherPaddings() {
		Reflections scanner = new Reflections(
				"org.bouncycastle.crypto.paddings");
		Set<Class<? extends BlockCipherPadding>> paddings = scanner
				.getSubTypesOf(BlockCipherPadding.class);
		for (Class<? extends BlockCipherPadding> i : paddings) {
			blockCipherPadding.put(i.getSimpleName(), i);
		}
		blockCipherPadding.put("PKCS5PADDING", PKCS7Padding.class);
	}

	public static int getKeySize(String algorithm) {
		if (algorithm.compareToIgnoreCase("des") == 0) {
			return DES_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("desede") == 0
				|| algorithm.compareToIgnoreCase("tripledes") == 0) {
			return DESEDE_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("aes") == 0) {
			return AES_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("twofish") == 0) {
			return DEFAULT;
		}
		if (algorithm.compareToIgnoreCase("cast5") == 0) {
			return CAST5_KEY_SIZE;
		}
		if (algorithm.compareToIgnoreCase("cast6") == 0) {
			return CAST6_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("blowfish")) {
			return BLOWFISH_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("CAMELLIA")) {
			return CAMELLIA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("GOST28147")) {
			return GOST28147_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("IDEA")) {
			return IDEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("NOEKEON")) {
			return NOEKEON_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("RC2")) {
			return RC2_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("RC5")) {
			return RC5_KEYSIZE;
		}
		if (algorithm.equalsIgnoreCase("RC6")) {
			return RC6_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SEED")) {
			return SEED_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SHACAL2")) {
			return SHACAL2_KEY_SIZE;
		}

		if (algorithm.equalsIgnoreCase("SERPENT")) {
			return SERPENT_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SKIPJACK")) {
			return SKIPJACK_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("TEA")) {
			return TEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("TWOFISH")) {
			return TWOFISH_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("XTEA")) {
			return XTEA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("RC4")
				|| algorithm.equalsIgnoreCase("ARC4")
				|| algorithm.equalsIgnoreCase("ARCFOUR")) {
			return RC4_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("HC128")) {
			return HC128_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("HC256")) {
			return HC256_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("CHACHA")) {
			return CHACHA_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("SALSA20")) {
			return SALSA20_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("XSAlSA20")) {
			return XSAlSA20_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("ISAAC")) {
			return ISAAC_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("VMPC")) {
			return VMPC_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("GRAINV1")) {
			return GRAINV1_KEY_SIZE;
		}
		if (algorithm.equalsIgnoreCase("GRAIN128")) {
			return GRAIN128_KEY_SIZE;
		}
		if (algorithm.matches("\\w+(\\W|\\S)?\\d+")) {
			Matcher num = Pattern.compile("\\d+").matcher(algorithm);
			while (num.find()) {
				return Integer.parseInt(num.group());
			}
		}
		return DEFAULT;
	}

	public static BlockCipher getBlockCipherEngine(String alg) {
		try {
			return blockCipherEngines.get(alg).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static BlockCipher wrapBlockCipherMode(BlockCipher engine,
			String mode) {
		try {
			Class<? extends BlockCipher> modeClass = blockCipherMode.get(mode);
			Constructor<?>[] modeCtors = modeClass.getConstructors();
			Constructor<? extends BlockCipher> modeCtor;
			if (modeCtors[0].getParameterTypes().length == 1) {
				modeCtor = modeClass.getConstructor(BlockCipher.class);
			} else {
				modeCtor = modeClass.getConstructor(BlockCipher.class,
						int.class);
			}

			BlockCipher modedEngine;
			if (modeCtor.getParameterTypes().length == 1) {
				modedEngine = modeCtor.newInstance(engine);
			} else {
				modedEngine = modeCtor.newInstance(engine,
						engine.getBlockSize() * 8);
			}
			return modedEngine;
		} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.fillInStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static BlockCipherPadding getBlockCipherPadding(String paddingName) {
		BlockCipherPadding padding;
		try {
			padding = blockCipherPadding.get(paddingName).newInstance();
			return padding;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Digest getDigester(String digestName) {
		try {
			return digesters.get(digestName).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Set<String> getSupportedBlockCipher() {
		Set<String> result = new TreeSet<String>();
		for (Entry<String, Class<? extends BlockCipher>> iter : blockCipherEngines
				.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	public static Set<String> getSupportedBlockCipherMode() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends BlockCipher>> iter : blockCipherMode
				.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	public static Set<String> getSupportedBlockCipherPadding() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends BlockCipherPadding>> iter : blockCipherPadding
				.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	public static Set<String> getSupportedDigest() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends Digest>> iter : digesters.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

	public static Set<String> getGmacAlgorithms() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends BlockCipher>> iter : blockCipherEngines
				.entrySet()) {
			try {
				if (iter.getValue().newInstance().getBlockSize() == 16) {
					result.add(iter.getKey());
				}
			} catch (InstantiationException | IllegalAccessException e) {
				continue;
			}
		}
		return result;
	}
}
