package net.viperfish.journal.framework;

import java.util.Properties;

import net.viperfish.journal.auth.AuthenticationManager;

public abstract class UserInterface extends Subject {

	private AuthenticationManager auth;

	public abstract void run();

	public abstract void setup();

	public abstract Properties getConfig();

	public abstract void setConfig(Properties p);

	public void setAuthManager(AuthenticationManager auth) {
		this.auth = auth;
	}

	protected boolean authenticate(String password) {
		return this.auth.verify(password);
	}

	protected boolean isPasswordSet() {
		return auth.isPasswordSet();
	}

	protected void setPassword(String password) {
		auth.setPassword(password);
	}
}
