package net.viperfish.journal.operation;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

/**
 * edits an entry in the system
 * 
 * @author sdai
 *
 */
public abstract class EditEntryOperation implements Operation {

	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	/**
	 * edit the journal
	 * 
	 * @param e
	 *            to edit
	 */
	protected abstract void edit(Journal e);

	public EditEntryOperation(Long id) {
		this.id = id;
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

	@Override
	public void execute() {
		Journal e = db.getEntry(id);
		edit(e);
		indexer.delete(id);
		indexer.add(e);
		db.updateEntry(id, e);

	}

}
