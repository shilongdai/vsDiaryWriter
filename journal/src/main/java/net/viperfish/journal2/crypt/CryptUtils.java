package net.viperfish.journal2.crypt;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public enum CryptUtils {
	INSTANCE;

	public byte[] transformData(PaddedBufferedBlockCipher cipher, byte[] data)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		// allocate a buffer that's big enough
		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		// update the cipher
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		// copy the actual result into a array of the correct length
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}

	public byte[] transformData(AEADBlockCipher cipher, byte[] data, byte[] additional)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {

		cipher.processAADBytes(additional, 0, additional.length);

		// allocate a buffer that's big enough
		int minSize = cipher.getOutputSize(data.length);
		byte[] outBuf = new byte[minSize];
		// update the cipher
		int length1 = cipher.processBytes(data, 0, data.length, outBuf, 0);
		int length2 = cipher.doFinal(outBuf, length1);
		// copy the actual result into a array of the correct length
		int actualLength = length1 + length2;
		byte[] result = new byte[actualLength];
		System.arraycopy(outBuf, 0, result, 0, result.length);
		return result;
	}

	public byte[] transformData(byte[] data, StreamCipher engine) {
		byte[] result = new byte[data.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = engine.returnByte(data[i]);
		}
		return result;
	}

	public byte[] kdfKey(String password, byte[] salt, int length, Digest hash) {
		PBEParametersGenerator gen = new PKCS12ParametersGenerator(hash);
		gen.init(PBEParametersGenerator.PKCS12PasswordToBytes(password.toCharArray()), salt, 256000);
		KeyParameter param = (KeyParameter) gen.generateDerivedParameters(length);
		return param.getKey();
	}

	public byte[] ecbCrypt(byte[] data, byte[] key, BlockCipher engine)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine, new PKCS7Padding());
		cipher.init(true, new KeyParameter(key));
		return transformData(cipher, data);
	}

	public byte[] ecbDecrypt(byte[] data, byte[] key, BlockCipher engine)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(engine, new PKCS7Padding());
		cipher.init(false, new KeyParameter(key));
		return transformData(cipher, data);
	}

	public byte[] calculateMac(byte[] data, Mac mac) {
		for (byte i : data) {
			mac.update(i);
		}
		byte[] buffer = new byte[4096];
		int length = mac.doFinal(buffer, 0);
		byte[] result = new byte[length];
		System.arraycopy(buffer, 0, result, 0, length);
		return result;
	}

	public byte[] generateNonce(int length) {
		long systemTime = System.currentTimeMillis();
		byte[] timeBuffer = ByteBuffer.allocate(Long.BYTES).putLong(systemTime).array();
		if (timeBuffer.length >= length) {
			byte[] partialTime = new byte[length];
			System.arraycopy(timeBuffer, 0, partialTime, 0, length);
		}
		SecureRandom rand = new SecureRandom();
		byte[] randBuffer = new byte[length - Long.BYTES];
		rand.nextBytes(randBuffer);
		byte[] result = new byte[length];
		System.arraycopy(timeBuffer, 0, result, 0, Long.BYTES);
		System.arraycopy(randBuffer, 0, result, Byte.SIZE, randBuffer.length);
		return result;
	}
}
