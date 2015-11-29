package net.viperfish.journal.operation;

import net.viperfish.journal.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class AddEntryOperation implements Operation {

	private Journal toAdd;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public AddEntryOperation(Journal add) {
		this.toAdd = add;
		db = Configuration.getDataSourceFactory().createDatabaseObject();
		indexer = Configuration.getIndexerFactory().createIndexer();
	}

	@Override
	public void execute() {
		db.addEntry(toAdd);
		indexer.add(toAdd);
	}

}
