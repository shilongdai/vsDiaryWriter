package net.viperfish.journal.auth;

import java.io.File;

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
	 * set the data directory of the application for persistence purpose
	 * 
	 * @param dataDir
	 *            the data directory
	 */
	public void setDataDir(File dataDir);
}
