package net.viperfish.journal.ui;

import net.viperfish.journal.framework.AuthenticationManager;

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
	public abstract void setup() throws TerminationControlFlowException;

	/**
	 * prompt password
	 * 
	 * @see UserInterface#authenticate(String)
	 * @return the valid password
	 */
	public abstract String promptPassword() throws TerminationControlFlowException;

	public abstract void setFirstPassword() throws TerminationControlFlowException;

	/**
	 * set the authenticator for this interface
	 * 
	 * @param auth
	 *            the authenticator
	 * @deprecated
	 */
	@Deprecated
	public void setAuthManager(AuthenticationManager auth) {
		this.auth = auth;
	}

	/**
	 * authenticate the password
	 * 
	 * @param password
	 *            password
	 * @deprecated
	 * @return if it's valid
	 */
	@Deprecated
	protected boolean authenticate(String password) {
		return this.auth.verify(password);
	}

	/**
	 * set a password
	 * 
	 * @param password
	 *            the password to set
	 * @deprecated
	 */
	@Deprecated
	protected void setPassword(String password) {
		auth.setPassword(password);
	}
}
