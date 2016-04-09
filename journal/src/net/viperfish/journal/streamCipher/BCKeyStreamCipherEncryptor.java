package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class BCKeyStreamCipherEncryptor implements StreamCipherEncryptor {

	private StreamCipher engine;

	BCKeyStreamCipherEncryptor(StreamCipher engine) {
		this.engine = engine;
	}

	private byte[] process(byte[] data, StreamCipher engine) {
		byte[] result = new byte[data.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = engine.returnByte(data[i]);
		}
		engine.reset();
		return result;
	}

	@Override
	public byte[] encrypt(byte[] data, byte[] key, byte[] iv) {
		engine.init(true, new KeyParameter(key));
		return process(data, engine);
	}

	@Override
	public byte[] decrypt(byte[] data, byte[] key, byte[] iv) {
		engine.init(false, new KeyParameter(key));
		return process(data, engine);
	}

}
