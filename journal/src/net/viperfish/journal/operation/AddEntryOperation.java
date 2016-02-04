package net.viperfish.journal.operation;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public class AddEntryOperation implements Operation {

	private Journal toAdd;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public AddEntryOperation(Journal add) {
		this.toAdd = add;
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

	@Override
	public void execute() {
		toAdd = db.addEntry(toAdd);
		indexer.add(toAdd);
	}

}
