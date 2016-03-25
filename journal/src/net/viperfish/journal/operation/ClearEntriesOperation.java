package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

class ClearEntriesOperation extends InjectedOperation {

	public ClearEntriesOperation() {
	}

	@Override
	public void execute() {
		indexer().clear();
		db().clear();
	}

}
