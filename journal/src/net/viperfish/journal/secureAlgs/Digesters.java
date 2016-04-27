package net.viperfish.journal.secureAlgs;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
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
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;

/**
 * utils for Bouncy Castle digester
 * 
 * @author sdai
 *
 */
public final class Digesters {

	private static Map<String, Class<? extends Digest>> digesters;

	static {
		digesters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		initDigesters();
	}

	private static void initDigesters() {
		digesters.put("Blake2b", Blake2bDigest.class);
		digesters.put("GOST3411", GOST3411Digest.class);
		digesters.put("Keccak", KeccakDigest.class);
		digesters.put("MD2", MD2Digest.class);
		digesters.put("MD4", MD4Digest.class);
		digesters.put("MD5", MD5Digest.class);
		digesters.put("RIPEMD128", RIPEMD128Digest.class);
		digesters.put("RIPEMD160", RIPEMD160Digest.class);
		digesters.put("RIPEMD256", RIPEMD256Digest.class);
		digesters.put("RIPEMD320", RIPEMD320Digest.class);
		digesters.put("SHA1", SHA1Digest.class);
		digesters.put("SHA224", SHA224Digest.class);
		digesters.put("SHA256", SHA256Digest.class);
		digesters.put("SHA384", SHA384Digest.class);
		digesters.put("SHA3-512", SHA3Digest.class);
		digesters.put("SHA3-256", SHA3Digest.class);
		digesters.put("SHA3-224", SHA3Digest.class);
		digesters.put("SHA3-384", SHA3Digest.class);
		digesters.put("SHA512", SHA512Digest.class);
		digesters.put("SHAKE-128", SHAKEDigest.class);
		digesters.put("SHAKE-256", SHAKEDigest.class);
		digesters.put("Skein256", SkeinDigest.class);
		digesters.put("Skein512", SkeinDigest.class);
		digesters.put("Skein1024", SkeinDigest.class);
		digesters.put("SM3", SM3Digest.class);
		digesters.put("Tiger", TigerDigest.class);
		digesters.put("Whirlpool", WhirlpoolDigest.class);
	}

	public static Digest getDigester(String digestName) {
		try {
			Digest result = null;
			switch (digestName) {
			case "SHA3-512": {
				result = new SHA3Digest(512);
				break;
			}
			case "SHA3-256": {
				result = new SHA3Digest(256);
				break;
			}
			case "SHA3-224": {
				result = new SHA3Digest(224);
				break;
			}
			case "SHA3-384": {
				result = new SHA3Digest(384);
				break;
			}
			case "SHAKE-128": {
				result = new SHAKEDigest(128);
				break;
			}
			case "SHAKE-256": {
				result = new SHAKEDigest(256);
				break;
			}
			case "Skein256": {
				result = new SkeinDigest(SkeinDigest.SKEIN_256, 256);
				break;
			}
			case "Skein512": {
				result = new SkeinDigest(SkeinDigest.SKEIN_512, 512);
				break;
			}
			case "Skein1024": {
				result = new SkeinDigest(SkeinDigest.SKEIN_1024, 1024);
				break;
			}
			default: {
				result = digesters.get(digestName).newInstance();
			}
			}
			return result;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static Set<String> getSupportedDigest() {
		Set<String> result = new TreeSet<>();
		for (Entry<String, Class<? extends Digest>> iter : digesters.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

}
