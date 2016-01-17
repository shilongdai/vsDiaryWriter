package net.viperfish.journal.operation;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Operation;

public class ChangeAuthManagerOperation implements Operation {

	private String toChange;
	private AuthenticationManager authManager;

	public ChangeAuthManagerOperation(String change) {
		this.toChange = change;
		authManager = ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT));
	}

	@Override
	public void execute() {
		AuthenticationManager newAuth = ComponentProvider.getAuthManager(toChange);
		newAuth.setPassword(authManager.getPassword());
		authManager.clear();
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, toChange);
	}

}
