package net.viperfish.journal.operation;

import net.viperfish.journal.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public abstract class EditEntryOperation implements Operation {

	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	protected abstract void edit(Journal e);

	public EditEntryOperation(Long id) {
		this.id = id;
		db = Configuration.getDataSourceFactory().createDatabaseObject();
		indexer = Configuration.getIndexerFactory().createIndexer();
	}

	@Override
	public void execute() {
		Journal e = db.getEntry(id);
		edit(e);
		Journal updated = db.updateEntry(id, e);
		indexer.delete(id);
		indexer.add(updated);
	}

}
