package net.viperfish.journal.framework;

/**
 * a authenticator responsible for setting, and validating password
 * 
 * @author sdai
 *
 */
public interface AuthenticationManager {

	/**
	 * clear the current password
	 */
	void clear();

	/**
	 * set a new password
	 * 
	 * @param pass
	 *            the password
	 */
	void setPassword(String pass);

	/**
	 * reload the password from a storage
	 */
	void reload();

	/**
	 * get the unhashed password
	 * 
	 * @return the plain password
	 */
	public String getPassword();

	/**
	 * Authenticate a password
	 * 
	 * @param string
	 *            the password
	 * @return whether the password is correct
	 */
	boolean verify(String string);

}
