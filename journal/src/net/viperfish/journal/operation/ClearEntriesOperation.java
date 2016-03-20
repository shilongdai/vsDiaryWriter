package net.viperfish.journal.operation;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.utils.index.Indexer;

public class ClearEntriesOperation implements Operation {

	private Indexer<Journal> indexer;
	private EntryDatabase db;

	public ClearEntriesOperation() {
		indexer = Indexers.INSTANCE.getIndexer();
		db = EntryDatabases.INSTANCE.getEntryDatabase();
	}

	@Override
	public void execute() {
		indexer.clear();
		db.clear();
	}

}
