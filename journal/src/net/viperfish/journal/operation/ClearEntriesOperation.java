package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * clears all entry from database and indexer
 * 
 * @author sdai
 *
 */
final class ClearEntriesOperation extends InjectedOperation {

	ClearEntriesOperation() {
	}

	@Override
	public void execute() {
		try {
			db().clear();
		} catch (FailToSyncEntryException e) {
			OperationErrorException fail = new OperationErrorException("Cannot clear database:" + e.getMessage());
			fail.initCause(e);
			throw fail;
		}

		indexer().clear();
	}

}
