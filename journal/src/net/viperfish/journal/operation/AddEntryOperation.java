package net.viperfish.journal.operation;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public class AddEntryOperation implements Operation {

	private Journal toAdd;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private JournalTransformer t;

	public AddEntryOperation(Journal add) {
		this.toAdd = add;
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		indexer = ComponentProvider.getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
		t = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));

	}

	@Override
	public void execute() {
		String password = ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT))
				.getPassword();
		t.setPassword(password);
		Journal encrypted = t.encryptJournal(toAdd);
		encrypted = db.addEntry(encrypted);
		toAdd.setId(encrypted.getId());
		indexer.add(toAdd);
	}

}
