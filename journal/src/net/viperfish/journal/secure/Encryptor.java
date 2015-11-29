package net.viperfish.journal.secure;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public abstract class Encryptor {

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
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public abstract byte[] getKey();

	public abstract void setKey(byte[] key);

	public abstract byte[] getIv();

	public abstract void setIv(byte[] iv);

	public abstract Set<String> getSupported();

	public abstract byte[] encrypt(byte[] text) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException;

	public abstract byte[] decrypt(byte[] cipher) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException;

	public int getKeySize() {
		String[] parts = mode.split("/");
		if (parts[0].compareToIgnoreCase("des") == 0) {
			return DES_KEY_SIZE;
		}
		if (parts[0].compareToIgnoreCase("desede") == 0
				|| parts[0].compareToIgnoreCase("tripledes") == 0) {
			return DESEDE_KEY_SIZE;
		}
		if (parts[0].compareToIgnoreCase("aes") == 0) {
			return AES_KEY_SIZE;
		}
		if (parts[0].compareToIgnoreCase("twofish") == 0) {
			return DEFAULT;
		}
		if (parts[0].compareToIgnoreCase("cast5") == 0) {
			return CAST5_KEY_SIZE;
		}
		if (parts[0].compareToIgnoreCase("cast6") == 0) {
			return CAST6_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("blowfish")) {
			return BLOWFISH_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("CAMELLIA")) {
			return CAMELLIA_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("GOST28147")) {
			return GOST28147_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("IDEA")) {
			return IDEA_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("NOEKEON")) {
			return NOEKEON_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("RC2")) {
			return RC2_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("RC5")) {
			return RC5_KEYSIZE;
		}
		if (parts[0].equalsIgnoreCase("RC6")) {
			return RC6_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("SEED")) {
			return SEED_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("SHACAL2")) {
			return SHACAL2_KEY_SIZE;
		}

		if (parts[0].equalsIgnoreCase("SERPENT")) {
			return SERPENT_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("SKIPJACK")) {
			return SKIPJACK_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("TEA")) {
			return TEA_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("TWOFISH")) {
			return TWOFISH_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("XTEA")) {
			return XTEA_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("RC4")
				|| parts[0].equalsIgnoreCase("ARC4")
				|| parts[0].equalsIgnoreCase("ARCFOUR")) {
			return RC4_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("HC128")) {
			return HC128_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("HC256")) {
			return HC256_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("CHACHA")) {
			return CHACHA_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("SALSA20")) {
			return SALSA20_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("XSAlSA20")) {
			return XSAlSA20_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("ISAAC")) {
			return ISAAC_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("VMPC")) {
			return VMPC_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("GRAINV1")) {
			return GRAINV1_KEY_SIZE;
		}
		if (parts[0].equalsIgnoreCase("GRAIN128")) {
			return GRAIN128_KEY_SIZE;
		}
		if (parts[0].matches("\\w+(\\W|\\S)?\\d+")) {
			Matcher num = Pattern.compile("\\d+").matcher(parts[0]);
			while (num.find()) {
				return Integer.parseInt(num.group());
			}
		}
		return DEFAULT;
	}

}