package net.viperfish.journal.operation;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;

/**
 * edits an entry in the system
 * 
 * @author sdai
 *
 */
public abstract class EditEntryOperation extends InjectedOperation {

	private Long id;

	/**
	 * edit the journal
	 * 
	 * @param e
	 *            to edit
	 */
	protected abstract void edit(Journal e);

	public EditEntryOperation(Long id) {
		this.id = id;

	}

	@Override
	public void execute() {
		Journal e = db().getEntry(id);
		edit(e);
		indexer().delete(id);
		indexer().add(e);
		db().updateEntry(id, e);

	}

}
