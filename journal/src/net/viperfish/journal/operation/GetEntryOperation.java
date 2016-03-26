package net.viperfish.journal.operation;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * gets an entry from the system
 * 
 * @author sdai
 *
 */
final class GetEntryOperation extends OperationWithResult<Journal> {

	private Long id;

	GetEntryOperation(Long id) {
		this.id = id;

	}

	@Override
	public void execute() {
		Journal e = null;
		try {
			e = db().getEntry(id);
		} finally {
			setResult(e);
		}
	}

}
