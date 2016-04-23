package net.viperfish.journal.secureAlgs;

import net.viperfish.journal.framework.errors.CipherException;

/**
 * a cipher calculator
 * 
 * @author sdai
 *
 */
public interface BlockCipherEncryptor {

	/**
	 * encrypt text using a key and an IV. The key is mandatory, but the IV can
	 * be self generated
	 * 
	 * @param text
	 *            the text to encrypt
	 * @return the ciphered text
	 */
	public byte[] encrypt(byte[] text, byte[] key, byte[] iv) throws CipherException;

	/**
	 * decrypt text using a key and an IV. The key is mandatory, but the IV can
	 * be self generated
	 * 
	 * @param cipher
	 *            the cipher to decrypt
	 * @return the plain text
	 */
	public byte[] decrypt(byte[] cipher, byte[] key, byte[] iv) throws CipherException;

	/**
	 * gets the key size of the current mode
	 * 
	 * @return
	 */
	public int getKeySize();

	public int getBlockSize();
}