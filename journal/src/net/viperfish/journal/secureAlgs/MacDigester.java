package net.viperfish.journal.secureAlgs;

/**
 * a cryptographic mac calculator
 * 
 * @author sdai
 *
 */
public interface MacDigester {

	/**
	 * get the current mode
	 * 
	 * @return the mode
	 */
	public abstract String getMode();

	/**
	 * set the mode to use
	 * 
	 * @param mode
	 *            the mode to use
	 */
	public abstract void setMode(String mode);

	/**
	 * get the key that it is using for mac calculation
	 * 
	 * @return the active key
	 */
	public abstract byte[] getKey();

	/**
	 * set the key that it will use for mac calculation
	 * 
	 * @param key
	 *            the key that will be used
	 */
	public abstract void setKey(byte[] key);

	/**
	 * get the current IV for mac calculation
	 * 
	 * @return the IV
	 */
	public abstract byte[] getIv();

	/**
	 * set the IV that it will use for future calculations
	 * 
	 * @param iv
	 *            the IV to use
	 */
	public abstract void setIv(byte[] iv);

	/**
	 * calculates a mac
	 * 
	 * @param data
	 *            the data to calculate mac from
	 * @return the mac of the data
	 */
	public abstract byte[] calculateMac(byte[] data);

	/**
	 * get the length requirement of the IV for the current mode
	 * 
	 * @return the length of IV
	 */
	public abstract int getIvLength();

	/**
	 * get the length requirement of the key for the current mode
	 * 
	 * @return the length of the key
	 */
	public abstract int getKeyLength();

}