package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

public class SetPasswordOperation extends InjectedOperation {

	private String password;

	public SetPasswordOperation(String pass) {
		this.password = pass;
	}

	@Override
	public void execute() {
		auth().setPassword(password);

	}

}
