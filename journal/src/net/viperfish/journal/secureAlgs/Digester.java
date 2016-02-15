package net.viperfish.journal.secureAlgs;

/**
 * a hash calculator
 * 
 * @author sdai
 *
 */
public interface Digester {

	/**
	 * get the type of digester
	 * 
	 * @return the mode of the digester
	 */
	public abstract String getMode();

	/**
	 * set the mode of the digester
	 * 
	 * @param mode
	 *            the mode of the digester
	 */
	public abstract void setMode(String mode);

	/**
	 * calculate a digest from the parameter
	 * 
	 * @param text
	 *            the data to digest
	 * @return the digest output
	 */
	public abstract byte[] digest(byte[] text);

}