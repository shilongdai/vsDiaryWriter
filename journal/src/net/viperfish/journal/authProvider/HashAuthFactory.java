package net.viperfish.journal.authProvider;

import java.io.File;

import net.viperfish.journal.framework.AuthenticationManager;

public class HashAuthFactory implements AuthenticationManagerFactory {
	private HashAuthManager auth;
	private File dataDir;

	public HashAuthFactory() {

	}

	@Override
	public AuthenticationManager getAuthenticator() {
		if (auth == null) {
			auth = new HashAuthManager(dataDir);
		}
		return auth;
	}

	@Override
	public void setDataDir(File dataDir) {
		this.dataDir = dataDir;

	}

	@Override
	public AuthenticationManager newAuthenticator() {
		return new HashAuthManager(dataDir);
	}

}
