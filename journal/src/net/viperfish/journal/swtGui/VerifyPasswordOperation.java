package net.viperfish.journal.swtGui;

import net.viperfish.journal.framework.OperationWithResult;

final class VerifyPasswordOperation extends OperationWithResult<Boolean> {

	private String password;

	VerifyPasswordOperation(String password) {
		this.password = password;
	}

	@Override
	public void execute() {
		setResult(auth().verify(password));
	}

}
