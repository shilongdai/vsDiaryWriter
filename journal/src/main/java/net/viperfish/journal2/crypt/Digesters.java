package net.viperfish.journal2.crypt;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.MD2Digest;
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

    public static Digest getDigester(String digestName) throws NoSuchAlgorithmException {
        Digest result = null;
        switch (digestName) {
            case "Blake2b": {
                return new Blake2bDigest();
            }
            case "GOST3411": {
                return new GOST3411Digest();
            }
            case "Keccak": {
                return new KeccakDigest();
            }
            case "MD2": {
                return new MD2Digest();
            }
            case "MD4": {
                return new MD2Digest();
            }
            case "MD5": {
                return new MD5Digest();
            }
            case "RIPEMD128": {
                return new RIPEMD128Digest();
            }
            case "RIPEMD160": {
                return new RIPEMD160Digest();
            }
            case "RIPEMD256": {
                return new RIPEMD256Digest();
            }
            case "RIPEMD320": {
                return new RIPEMD320Digest();
            }
            case "SHA1": {
                return new SHA1Digest();
            }
            case "SHA224": {
                return new SHA224Digest();
            }
            case "SHA256": {
                return new SHA256Digest();
            }
            case "SHA384": {
                return new SHA384Digest();
            }
            case "SHA512": {
                return new SHA512Digest();
            }
            case "SM3": {
                return new SM3Digest();
            }
            case "Tiger": {
                return new TigerDigest();
            }
            case "Whirlpool": {
                return new WhirlpoolDigest();
            }
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
                throw new NoSuchAlgorithmException(digestName);
            }
        }
        return result;
    }

    public static Set<String> getSupportedDigest() {
        return new HashSet<>(Arrays.asList("Blake2b", "GOST3411", "Keccak", "MD2", "MD4", "MD5", "RIPEMD128", "RIPEMD160", "RIPEMD256", "RIPEMD320", "SHA1", "SHA224", "SHA256", "SHA384", "SHA512", "SM3", "Tiger", "Whirlpool", "SHA3-512", "SHA3-256", "SHA3-224", "SHA3-384", "SHAKE-128", "SHAKE-256", "Skein256", "Skein512", "Skein1024"));
    }

}
