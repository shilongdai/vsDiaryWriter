package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

/**
 * set the password (For the first time)
 * 
 * @author sdai
 *
 */
class SetPasswordOperation extends InjectedOperation {

	private String password;

	public SetPasswordOperation(String pass) {
		this.password = pass;
	}

	@Override
	public void execute() {
		auth().setPassword(password);

	}

}
