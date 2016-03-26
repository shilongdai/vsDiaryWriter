package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

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
		indexer().clear();
		db().clear();
	}

}
