package net.viperfish.journal.secureAlgs;

/**
 * a PBKDF2 KDF calculator
 * 
 * @author sdai
 *
 */
public interface PBKDF2KeyGenerator {

	/**
	 * set the name of the hashing algorithm to use
	 * 
	 * @param digest
	 *            the name of the hashing algorithm
	 */
	public void setDigest(String digest);

	/**
	 * get the current hashing algorithm in use
	 * 
	 * @return the name of the current hashing algorithm
	 */
	public String getDigest();

	/**
	 * set the number of iteration to go
	 * 
	 * @param iteration
	 *            the number of iterations
	 */
	public void setIteration(int iteration);

	/**
	 * get the current number of iterations to compute
	 * 
	 * @return the current number of iterations
	 */
	public int getIteration();

	/**
	 * set the salt to use
	 * 
	 * @param salt
	 *            the salt
	 */
	public void setSalt(byte[] salt);

	/**
	 * get the current salt
	 * 
	 * @return the current salt
	 */
	public byte[] getSalt();

	/**
	 * generate a key
	 * 
	 * @param password
	 *            the password to derive key from
	 * @param length
	 *            the length of the generated key
	 * @return the generated key
	 */
	public byte[] generate(String password, int length);

	public int getDigestSize();
}
