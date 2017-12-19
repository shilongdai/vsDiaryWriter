package net.viperfish.journal2.crypt;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.Grainv1Engine;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;

import net.viperfish.journal2.core.TypeCache;

public enum StreamCipherEncryptors {
	INSTANCE;

	private final static int RC4_KEYSIZE = 512;
	private final static int HC128_KEYSIZE = 128;
	private final static int HC256_KEYSIZE = 256;
	private final static int ChaCha_KEYSIZE = 256;
	private final static int Salsa20_KEYSIZE = 256;
	private final static int XSalsa20_KEYSIZE = 256;
	private final static int ISAAC_KEYSIZE = 512;
	private final static int VMPC_KEYSIZE = 512;
	private final static int Grainv1_KEYSIZE = 80;
	private final static int Grain128_KEYSIZE = 128;

	private final static int ChaCha_IV = 64;
	private final static int Salsa20_IV = 64;
	private final static int XSalsa20_IV = 192;
	private final static int Grainv1_IV = 64;
	private final static int Grain128_IV = 96;
	private final static int HC256_IV = 256;
	private final static int HC128_IV = 128;
	private final static int VMPC_IV = 512;

	private Map<String, Class<? extends StreamCipher>> ciphers;
	private Map<String, Integer> keysizes;
	private Map<String, Integer> ivs;

	private StreamCipherEncryptors() {
		new TypeCache();
		ciphers = new TreeMap<>();
		keysizes = new HashMap<>();
		ivs = new HashMap<>();
		initCiphers();
		initKeySize();
		initIV();
	}

	private void initCiphers() {
		ciphers.put("RC4", RC4Engine.class);
		ciphers.put("HC128", HC128Engine.class);
		ciphers.put("HC256", HC256Engine.class);
		ciphers.put("ChaCha", ChaChaEngine.class);
		ciphers.put("Salsa20", Salsa20Engine.class);
		ciphers.put("XSalsa20", XSalsa20Engine.class);
		ciphers.put("ISACC", ISAACEngine.class);
		ciphers.put("VMPC", VMPCEngine.class);
		ciphers.put("Grainv1", Grainv1Engine.class);
		ciphers.put("Grain128", Grain128Engine.class);
	}

	private void initKeySize() {
		keysizes.put("RC4", RC4_KEYSIZE);
		keysizes.put("HC128", HC128_KEYSIZE);
		keysizes.put("HC256", HC256_KEYSIZE);
		keysizes.put("ChaCha", ChaCha_KEYSIZE);
		keysizes.put("Salsa20", Salsa20_KEYSIZE);
		keysizes.put("XSalsa20", XSalsa20_KEYSIZE);
		keysizes.put("ISACC", ISAAC_KEYSIZE);
		keysizes.put("VMPC", VMPC_KEYSIZE);
		keysizes.put("Grainv1", Grainv1_KEYSIZE);
		keysizes.put("Grain128", Grain128_KEYSIZE);
	}

	private void initIV() {
		ivs.put("ChaCha", ChaCha_IV);
		ivs.put("Salsa20", Salsa20_IV);
		ivs.put("XSalsa20", XSalsa20_IV);
		ivs.put("Grainv1", Grainv1_IV);
		ivs.put("Grain128", Grain128_IV);
		ivs.put("HC256", HC256_IV);
		ivs.put("HC128", HC128_IV);
		ivs.put("VMPC", VMPC_IV);
	}

	public StreamCipher getEncryptor(String cipher) throws NoSuchAlgorithmException {
		switch (cipher) {
		case "RC4": {
			return new RC4Engine();
		}
		case "HC128": {
			return new HC128Engine();
		}
		case "HC256": {
			return new HC256Engine();
		}
		case "ChaCha": {
			return new ChaChaEngine();
		}
		case "Salsa20": {
			return new Salsa20Engine();
		}
		case "XSalsa20": {
			return new XSalsa20Engine();
		}
		case "ISACC": {
			return new ISAACEngine();
		}
		case "VMPC": {
			return new VMPCEngine();
		}
		case "Grainv1": {
			return new Grainv1Engine();
		}
		case "Grain128": {
			return new Grain128Engine();
		}
		default: {
			throw new NoSuchAlgorithmException(cipher);
		}
		}
	}

	public int getKeySize(String cipher) {
		return keysizes.get(cipher);
	}

	public int getIVSize(String cipher) {
		Integer iv = ivs.get(cipher);
		if (iv == null) {
			return 0;
		}
		return iv;
	}

	public String[] getSupported() {
		return new String[] { "RC4", "HC128", "HC256", "ChaCha", "Salsa20", "XSalsa20", "ISACC", "VMPC", "Grainv1",
				"Grain128" };
	}

}
