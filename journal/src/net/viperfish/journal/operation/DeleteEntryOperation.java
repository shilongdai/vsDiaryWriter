package net.viperfish.journal.operation;

import net.viperfish.journal.Configuration;
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
		db = Configuration.getDataSourceFactory().createDatabaseObject();
		indexer = Configuration.getIndexerFactory().createIndexer();
	}

	@Override
	public void execute() {
		db.removeEntry(id);
		indexer.delete(id);
	}

}
