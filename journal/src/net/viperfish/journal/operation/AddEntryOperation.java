package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * adds an entry to the system
 * 
 * @author sdai
 *
 */
final class AddEntryOperation extends InjectedOperation {

	private Journal toAdd;

	AddEntryOperation(Journal add) {
		this.toAdd = add;

	}

	@Override
	public void execute() {
		// add to database and indexer
		try {
			toAdd = db().addEntry(toAdd);
		} catch (FailToSyncEntryException e) {
			OperationErrorException oe = new OperationErrorException("Failed to add entry:" + e.getMessage());
			oe.initCause(e);
			throw oe;
		}
		indexer().add(toAdd);
	}

}
