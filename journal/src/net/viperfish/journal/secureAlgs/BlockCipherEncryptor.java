package net.viperfish.journal.secureAlgs;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

/**
 * a cipher calculator
 * 
 * @author sdai
 *
 */
public abstract class BlockCipherEncryptor {

	private String mode;

	/**
	 * get the mode of the encryptor
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * set the mode of the encryptor
	 * 
	 * @param mode
	 *            the mode
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * get the key that it is using for encryption
	 * 
	 * @return the key
	 */
	public abstract byte[] getKey();

	/**
	 * set the key to use for encryption
	 * 
	 * @param key
	 *            the key to use
	 */
	public abstract void setKey(byte[] key);

	/**
	 * get the IV that it's using for encryption
	 * 
	 * @return the IV
	 */
	public abstract byte[] getIv();

	/**
	 * set the IV to use for encryption
	 * 
	 * @param iv
	 *            the IV
	 */
	public abstract void setIv(byte[] iv);

	/**
	 * encrypt text using a key and an IV. The key is mandatory, but the IV can
	 * be self generated
	 * 
	 * @param text
	 *            the text to encrypt
	 * @return the ciphered text
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public abstract byte[] encrypt(byte[] text) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException;

	/**
	 * decrypt text using a key and an IV. The key is mandatory, but the IV can
	 * be self generated
	 * 
	 * @param cipher
	 *            the cipher to decrypt
	 * @return the plain text
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public abstract byte[] decrypt(byte[] cipher) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException;

	/**
	 * gets the key size of the current mode
	 * 
	 * @return
	 */
	public int getKeySize() {
		return BlockCiphers.getKeySize(mode.split("/")[0]);
	}

}