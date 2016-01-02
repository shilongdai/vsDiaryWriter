package net.viperfish.journal.operation;

import net.viperfish.journal.ConfigMapping;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.provider.ComponentProvider;
import net.viperfish.utils.index.Indexer;

public abstract class EditEntryOperation implements Operation {

	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private JournalTransformer t;

	protected abstract void edit(Journal e);

	public EditEntryOperation(Long id) {
		this.id = id;
		db = ComponentProvider
				.getEntryDatabase(JournalApplication.getConfiguration().getString(ConfigMapping.DB_COMPONENT));
		indexer = ComponentProvider
				.getIndexer(JournalApplication.getConfiguration().getString(ConfigMapping.INDEXER_COMPONENT));
		t = ComponentProvider
				.getTransformer(JournalApplication.getConfiguration().getString(ConfigMapping.TRANSFORMER_COMPONENT));

	}

	@Override
	public void execute() {
		t.setPassword(ComponentProvider
				.getAuthManager(JournalApplication.getConfiguration().getString(ConfigMapping.AUTH_COMPONENT))
				.getPassword());
		Journal e = db.getEntry(id);
		e = t.decryptJournal(e);
		edit(e);
		indexer.delete(id);
		indexer.add(e);
		e = t.encryptJournal(e);
		db.updateEntry(id, e);

	}

}
