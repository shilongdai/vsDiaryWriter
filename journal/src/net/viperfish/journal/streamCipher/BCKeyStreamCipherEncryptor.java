package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.StreamCipher;

public class BCKeyStreamCipherEncryptor implements StreamCipherEncryptor {

	private StreamCipher engine;
	private KeyConverter con;

	BCKeyStreamCipherEncryptor(StreamCipher engine, KeyConverter converter) {
		this.engine = engine;
		this.con = converter;
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
		engine.init(true, con.convertToParam(key, iv));
		return process(data, engine);
	}

	@Override
	public byte[] decrypt(byte[] data, byte[] key, byte[] iv) {
		engine.init(false, con.convertToParam(key, iv));
		return process(data, engine);
	}

}
