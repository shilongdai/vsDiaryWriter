package net.viperfish.journal.authProvider;

import java.io.File;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Provider;

public class ViperfishAuthProvider implements Provider<AuthenticationManager> {

	private AuthenticationManagerFactory fact;
	private File dataDir;

	public ViperfishAuthProvider() {
		dataDir = new File("secure");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		fact = new HashAuthFactory();
		fact.setDataDir(dataDir);
	}

	@Override
	public AuthenticationManager newInstance() {
		return fact.newAuthenticator();
	}

	@Override
	public AuthenticationManager getInstance() {
		return fact.getAuthenticator();
	}

	@Override
	public AuthenticationManager newInstance(String instance) {
		if (instance.equals("HashAuthentication")) {
			return fact.newAuthenticator();
		}
		return null;
	}

	@Override
	public AuthenticationManager getInstance(String instance) {
		if (instance.equals("HashAuthentication")) {
			return fact.getAuthenticator();
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "HashAuthentication" };
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {

	}

	@Override
	public void setDefaultInstance(String instance) {

	}

	@Override
	public String getDefaultInstance() {
		return "HashAuthentication";
	}

}
