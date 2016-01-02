package net.viperfish.journal.authProvider;

import java.io.File;

import net.viperfish.journal.framework.AuthenticationManager;

/**
 * a factory that will return an instance of a AuthenticationManager
 * 
 * @author sdai
 * @see AuthenticationManager
 */
public interface AuthenticationManagerFactory {
	/**
	 * get an instance of a AuthenticationManager
	 * 
	 * @return the authenticator
	 */
	public AuthenticationManager getAuthenticator();

	/**
	 * create a new instance of a AuthenticationManager
	 * 
	 * @return the created authenticator
	 */
	public AuthenticationManager newAuthenticator();

	/**
	 * set the data directory of the application for persistence purpose
	 * 
	 * @param dataDir
	 *            the data directory
	 */
	public void setDataDir(File dataDir);
}
