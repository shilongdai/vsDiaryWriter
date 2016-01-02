package net.viperfish.journal.ui;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.utils.config.ComponentConfig;

/**
 * the current meta interface for UI
 * 
 * @author sdai
 *
 */
public abstract class UserInterface {

	private AuthenticationManager auth;

	/**
	 * start the UI loop, should block until the user selected quit
	 */
	public abstract void run();

	/**
	 * will be called when the program first run
	 * 
	 * @see UserInterface#setPassword(String)
	 * @see Configuration
	 * @see ComponentConfig
	 */
	public abstract void setup();

	/**
	 * prompt password
	 * 
	 * @see UserInterface#authenticate(String)
	 * @return the valid password
	 */
	public abstract String promptPassword();

	/**
	 * set the authenticator for this interface
	 * 
	 * @param auth
	 *            the authenticator
	 */
	public void setAuthManager(AuthenticationManager auth) {
		this.auth = auth;
	}

	/**
	 * authenticate the password
	 * 
	 * @param password
	 *            password
	 * @return if it's valid
	 */
	protected boolean authenticate(String password) {
		return this.auth.verify(password);
	}

	/**
	 * get if the password is set, result delegated from authenticator
	 * 
	 * @see AuthenticationManager
	 * @return if the password is set
	 */
	protected boolean isPasswordSet() {
		return auth.isPasswordSet();
	}

	/**
	 * set a password
	 * 
	 * @param password
	 *            the password to set
	 */
	protected void setPassword(String password) {
		auth.setPassword(password);
	}
}
