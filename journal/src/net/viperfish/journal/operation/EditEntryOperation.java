package net.viperfish.journal.operation;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public abstract class EditEntryOperation implements Operation {

	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

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
