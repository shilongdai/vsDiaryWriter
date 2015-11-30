package net.viperfish.journal.auth;

import java.io.File;

public interface AuthenticationManagerFactory {
	public AuthenticationManager getAuthenticator();

	public void setDataDir(File dataDir);
}
