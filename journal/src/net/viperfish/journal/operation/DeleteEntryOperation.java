package net.viperfish.journal.operation;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class DeleteEntryOperation implements Operation {
	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public DeleteEntryOperation(Long i) {
		this.id = i;
		db = JournalApplication.getDataSourceFactory().createDatabaseObject();
		indexer = JournalApplication.getIndexerFactory().createIndexer();
	}

	@Override
	public void execute() {
		db.removeEntry(id);
		indexer.delete(id);
	}

}
