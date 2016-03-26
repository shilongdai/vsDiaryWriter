package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

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
		db().removeEntry(id);
		indexer().delete(id);
	}

}
