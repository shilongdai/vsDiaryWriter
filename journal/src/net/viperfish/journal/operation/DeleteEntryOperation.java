package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * deletes an entry from system
 * 
 * @author sdai
 *
 */
final class DeleteEntryOperation extends InjectedOperation {
	private Long id;

	DeleteEntryOperation(Long i) {
		this.id = i;
	}

	@Override
	public void execute() {
		try {
			db().removeEntry(id);
		} catch (FailToSyncEntryException e) {
			OperationErrorException fail = new OperationErrorException(
					"Cannot delete journal " + id + " from the database:" + e.getMessage());
			fail.initCause(e);
			throw fail;
		}
		indexer().delete(id);
	}

}
