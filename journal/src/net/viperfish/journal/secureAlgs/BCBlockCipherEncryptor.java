package net.viperfish.journal.secureAlgs;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * an encryptor based on Bouncy Castle's crypto API
 * 
 * @author sdai
 *
 */
public class BCBlockCipherEncryptor extends Encryptor {

	private byte[] key;
	private byte[] iv;
	private SecureRandom rand;

	public BCBlockCipherEncryptor() {
		rand = new SecureRandom();
	}

	/**
	 * creates the object that can encrypt
	 * 
	 * @return
	 */
	private PaddedBufferedBlockCipher initCipherSuite() {
		String[] parts = getMode().split("/");
		BlockCipher engine = BlockCiphers.getBlockCipherEngine(parts[0]);
		BlockCipher modedEngine = BlockCiphers.wrapBlockCipherMode(engine, parts[1]);
		BlockCipherPadding padding = BlockCiphers.getBlockCipherPadding(parts[2]);
		PaddedBufferedBlockCipher result = new PaddedBufferedBlockCipher(modedEngine, padding);
		return result;
	}

	/**
	 * the actual ciphering
	 * 
	 * @param cipher
	 *            the encyption object
	 * @param data
	 *            the data to encrypt
	 * @return the transformed data
	 * @throws DataLengthException
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	private byte[] transformData(PaddedBufferedBlockCipher cipher, byte[] data)
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
	public byte[] encrypt(byte[] text) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		PaddedBufferedBlockCipher encryptor = initCipherSuite();
		iv = new byte[encryptor.getBlockSize()];
		rand.nextBytes(iv);
		encryptor.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(encryptor, text);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] decrypt(byte[] cipher) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		PaddedBufferedBlockCipher decryptor = initCipherSuite();
		decryptor.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(decryptor, cipher);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			throw new RuntimeException(e);
		}
	}

}
