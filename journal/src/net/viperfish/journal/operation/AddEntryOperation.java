package net.viperfish.journal.operation;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class AddEntryOperation implements Operation {

	private Journal toAdd;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private JournalTransformer t;

	public AddEntryOperation(Journal add) {
		this.toAdd = add;
		db = ComponentProvider.getEntryDatabase();
		indexer = ComponentProvider.getIndexer();
		t = ComponentProvider.getTransformer();
		t.setPassword(JournalApplication.getPassword());
	}

	@Override
	public void execute() {
		Journal encrypted = t.encryptJournal(toAdd);
		encrypted = db.addEntry(encrypted);
		toAdd.setId(encrypted.getId());
		indexer.add(toAdd);
	}

}
