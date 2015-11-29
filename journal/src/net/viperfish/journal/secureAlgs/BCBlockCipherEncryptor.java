package net.viperfish.journal.secureAlgs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import net.viperfish.journal.secure.Encryptor;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.reflections.Reflections;

public class BCBlockCipherEncryptor extends Encryptor {

	private Map<String, Class<? extends BlockCipher>> engines;
	private Map<String, Class<? extends BlockCipher>> mode;
	private Map<String, Class<? extends BlockCipherPadding>> padding;
	private byte[] key;
	private byte[] iv;
	private SecureRandom rand;

	public BCBlockCipherEncryptor() {
		engines = new TreeMap<String, Class<? extends BlockCipher>>(
				String.CASE_INSENSITIVE_ORDER);
		mode = new TreeMap<String, Class<? extends BlockCipher>>(
				String.CASE_INSENSITIVE_ORDER);
		padding = new TreeMap<String, Class<? extends BlockCipherPadding>>(
				String.CASE_INSENSITIVE_ORDER);
		rand = new SecureRandom();
		initEngines();
		initModes();
		initPaddings();
	}

	private void initEngines() {
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
					engines.put(i.newInstance().getAlgorithmName(), i);
				} catch (InstantiationException | IllegalAccessException e) {
					continue;
				}
			}
		}
	}

	private void initModes() {
		Reflections scanner = new Reflections("org.bouncycastle.crypto.modes");
		Set<Class<? extends BlockCipher>> blockCipherModes = scanner
				.getSubTypesOf(BlockCipher.class);
		for (Class<? extends BlockCipher> i : blockCipherModes) {
			String name = i.getSimpleName();
			name = name.substring(0, name.lastIndexOf("BlockCipher"));
			mode.put(name, i);
		}
		mode.put("CFB", CFBBlockCipher.class);
		mode.put("GCFB", GCFBBlockCipher.class);
		mode.put("GOFB", GOFBBlockCipher.class);
		mode.put("OFB", OFBBlockCipher.class);
		mode.put("CTR", SICBlockCipher.class);
	}

	private void initPaddings() {
		Reflections scanner = new Reflections(
				"org.bouncycastle.crypto.paddings");
		Set<Class<? extends BlockCipherPadding>> paddings = scanner
				.getSubTypesOf(BlockCipherPadding.class);
		for (Class<? extends BlockCipherPadding> i : paddings) {
			padding.put(i.getSimpleName(), i);
		}
		padding.put("PKCS5PADDING", PKCS7Padding.class);
	}

	private PaddedBufferedBlockCipher initCipherSuite() {
		String[] parts = getMode().split("/");
		try {
			BlockCipher engine = engines.get(parts[0]).newInstance();
			Class<? extends BlockCipher> modeClass = mode.get(parts[1]);
			Constructor<?>[] modeCtors = modeClass.getConstructors();
			Constructor<? extends BlockCipher> modeCtor;
			if (modeCtors[0].getParameterCount() == 1) {
				modeCtor = modeClass.getConstructor(BlockCipher.class);
			} else {
				modeCtor = modeClass.getConstructor(BlockCipher.class,
						int.class);
			}

			BlockCipher modedEngine;
			if (modeCtor.getParameterCount() == 1) {
				modedEngine = modeCtor.newInstance(engine);
			} else {
				modedEngine = modeCtor.newInstance(engine,
						engine.getBlockSize() * 8);
			}
			BlockCipherPadding padding = this.padding.get(parts[2])
					.newInstance();
			PaddedBufferedBlockCipher result = new PaddedBufferedBlockCipher(
					modedEngine, padding);
			return result;
		} catch (InstantiationException | IllegalAccessException
				| NoSuchMethodException | SecurityException
				| IllegalArgumentException | InvocationTargetException e) {
			e.fillInStackTrace();
			throw new RuntimeException(e);
		}
	}

	private byte[] transformData(PaddedBufferedBlockCipher cipher, byte[] data)
			throws DataLengthException, IllegalStateException,
			InvalidCipherTextException {
		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public void setKey(byte[] key) {
		this.key = key;
	}

	@Override
	public byte[] getIv() {
		return iv;
	}

	@Override
	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	@Override
	public byte[] encrypt(byte[] text) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		PaddedBufferedBlockCipher encryptor = initCipherSuite();
		iv = new byte[encryptor.getBlockSize()];
		rand.nextBytes(iv);
		encryptor.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(encryptor, text);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipher) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException {
		PaddedBufferedBlockCipher decryptor = initCipherSuite();
		decryptor.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(decryptor, cipher);
		} catch (DataLengthException | IllegalStateException
				| InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<String> getSupported() {
		Set<String> result = new TreeSet<String>();
		for (Entry<String, Class<? extends BlockCipher>> iter : this.engines
				.entrySet()) {
			result.add(iter.getKey());
		}
		return result;
	}

}
