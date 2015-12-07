package net.viperfish.journal.auth;

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
	 * Authenticate a password
	 * 
	 * @param string
	 *            the password
	 * @return whether the password is correct
	 */
	boolean verify(String string);

	/**
	 * get if the password is set
	 * 
	 * @return if password set
	 */
	boolean isPasswordSet();

}
