package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;

/**
 * deletes an entry from system
 * 
 * @author sdai
 *
 */
class DeleteEntryOperation extends InjectedOperation {
	private Long id;

	public DeleteEntryOperation(Long i) {
		this.id = i;
	}

	@Override
	public void execute() {
		db().removeEntry(id);
		indexer().delete(id);
	}

}
