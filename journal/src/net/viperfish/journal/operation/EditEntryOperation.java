package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * edits an entry in the system
 * 
 * @author sdai
 *
 */
abstract class EditEntryOperation extends InjectedOperation {

	private Long id;

	/**
	 * edit the journal
	 * 
	 * @param e
	 *            to edit
	 */
	protected abstract void edit(Journal e);

	EditEntryOperation(Long id) {
		this.id = id;

	}

	@Override
	public void execute() {
		Journal e = db().getEntry(id);
		edit(e);

		try {
			db().updateEntry(id, e);
		} catch (FailToSyncEntryException e1) {
			OperationErrorException fail = new OperationErrorException(
					"Cannot update journal " + id + " in the database:" + e1.getMessage());
			fail.initCause(e1);
			throw fail;
		}

		indexer().delete(id);
		indexer().add(e);

	}

}
