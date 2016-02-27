package net.viperfish.journal.operation;

import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Operation;

public class SetPasswordOperation implements Operation {

	private String password;
	private AuthenticationManager auth;

	public SetPasswordOperation(String pass) {
		this.password = pass;
		auth = AuthManagers.INSTANCE.getAuthManager();
	}

	@Override
	public void execute() {
		auth.setPassword(password);

	}

}
