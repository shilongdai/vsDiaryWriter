package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

final class BCIVStreamCipherEncryptor implements StreamCipherEncryptor {

	private StreamCipher engine;

	BCIVStreamCipherEncryptor(StreamCipher engine) {
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
		engine.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		return process(data, engine);
	}

	@Override
	public byte[] decrypt(byte[] data, byte[] key, byte[] iv) {
		engine.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		return process(data, engine);
	}

}
