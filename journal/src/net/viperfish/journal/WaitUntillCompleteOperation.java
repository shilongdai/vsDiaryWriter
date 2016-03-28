package net.viperfish.journal;

import net.viperfish.journal.framework.OperationWithResult;

public class WaitUntillCompleteOperation extends OperationWithResult<Boolean> {

	@Override
	public void execute() {
		this.setResult(true);

	}

}
