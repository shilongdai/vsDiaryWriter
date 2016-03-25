package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

/**
 * clears all entry from database and indexer
 * 
 * @author sdai
 *
 */
class ClearEntriesOperation extends InjectedOperation {

	public ClearEntriesOperation() {
	}

	@Override
	public void execute() {
		indexer().clear();
		db().clear();
	}

}
