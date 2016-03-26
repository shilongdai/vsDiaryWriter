package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;

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
		toAdd = db().addEntry(toAdd);
		indexer().add(toAdd);
	}

}
