package net.viperfish.journal.operation;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public abstract class EditEntryOperation implements Operation {

	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private JournalTransformer t;

	protected abstract void edit(Journal e);

	public EditEntryOperation(Long id) {
		this.id = id;
		db = ComponentProvider.getEntryDatabase(JournalApplication.getSysConf().getProperty("DataStorage"));
		indexer = ComponentProvider.getIndexer(JournalApplication.getSysConf().getProperty("Indexer"));
		t = ComponentProvider.getTransformer();
		t.setPassword(JournalApplication.getPassword());
	}

	@Override
	public void execute() {
		Journal e = db.getEntry(id);
		e = t.decryptJournal(e);
		edit(e);
		indexer.delete(id);
		indexer.add(e);
		e = t.encryptJournal(e);
		db.updateEntry(id, e);

	}

}
