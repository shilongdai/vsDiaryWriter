package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * set the password (For the first time)
 * 
 * @author sdai
 *
 */
final class SetPasswordOperation extends InjectedOperation {

	private String password;

	SetPasswordOperation(String pass) {
		this.password = pass;
	}

	@Override
	public void execute() {
		try {
			auth().setPassword(password);
		} catch (FailToStoreCredentialException e) {
			OperationErrorException err = new OperationErrorException("Cannot set password:" + e.getMessage());
			throw err;
		}

	}

}
