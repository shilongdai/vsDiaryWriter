package net.viperfish.journal.framework;

import net.viperfish.journal.auth.AuthenticationManager;

public abstract class UserInterface {

	private AuthenticationManager auth;

	public abstract void run();

	public abstract void setup();

	public abstract String promptPassword();

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
