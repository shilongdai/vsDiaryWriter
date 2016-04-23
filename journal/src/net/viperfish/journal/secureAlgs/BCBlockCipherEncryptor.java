package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.viperfish.journal.framework.errors.CipherException;

/**
 * an encryptor based on Bouncy Castle's crypto API
 * 
 * @author sdai
 *
 */
final class BCBlockCipherEncryptor implements BlockCipherEncryptor {

	private int keySize;
	private int blockSize;
	private PaddedBufferedBlockCipher cipher;

	public final static class BCBlockCipherBuilder {
		private int keySize;
		private int blockSize;
		private BlockCipher cipher;
		private BlockCipherPadding padding;

		public int getKeySize() {
			return keySize;
		}

		public BCBlockCipherBuilder setKeySize(int keySize) {
			this.keySize = keySize;
			return this;
		}

		public int getBlockSize() {
			return blockSize;
		}

		public BCBlockCipherBuilder setBlockSize(int blockSize) {
			this.blockSize = blockSize;
			return this;
		}

		public BlockCipher getCipher() {
			return cipher;
		}

		public BCBlockCipherBuilder setCipher(BlockCipher cipher) {
			this.cipher = cipher;
			return this;
		}

		public BlockCipherPadding getPadding() {
			return padding;
		}

		public BCBlockCipherBuilder setPadding(BlockCipherPadding padding) {
			this.padding = padding;
			return this;
		}

		public BCBlockCipherEncryptor build() {
			BCBlockCipherEncryptor result = new BCBlockCipherEncryptor();
			result.blockSize = this.blockSize;
			result.keySize = this.keySize;
			result.cipher = new PaddedBufferedBlockCipher(cipher, padding);

			if (result.blockSize == 0) {
				throw new IllegalArgumentException("Block size must not be 0");
			}
			if (result.keySize == 0) {
				throw new IllegalArgumentException("Key size must not be 0");
			}

			return result;
		}

	}

	private BCBlockCipherEncryptor() {

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
	public byte[] encrypt(byte[] text, byte[] key, byte[] iv) throws CipherException {
		cipher.init(true, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(cipher, text);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			CipherException ce = new CipherException("Cannot encrypt:" + e.getMessage());
			ce.initCause(e);
			throw ce;
		}
	}

	@Override
	public byte[] decrypt(byte[] cipher, byte[] key, byte[] iv) throws CipherException {
		this.cipher.init(false, new ParametersWithIV(new KeyParameter(key), iv));
		try {
			return transformData(this.cipher, cipher);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			CipherException ce = new CipherException("Cannot decrypt:" + e.getMessage());
			ce.initCause(e);
			throw ce;
		}
	}

	@Override
	public int getKeySize() {
		return keySize;
	}

	@Override
	public int getBlockSize() {
		return blockSize;
	}

}
