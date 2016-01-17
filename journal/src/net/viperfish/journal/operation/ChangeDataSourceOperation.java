package net.viperfish.journal.operation;

import java.util.List;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public class ChangeDataSourceOperation implements Operation {

	private String newDataSource;
	private Indexer<Journal> indexer;
	private EntryDatabase db;
	private JournalTransformer tr;

	public ChangeDataSourceOperation(String changed) {
		this.newDataSource = changed;
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		indexer = ComponentProvider.getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		tr = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
	}

	@Override
	public void execute() {
		tr.setPassword(
				ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT)).getPassword());
		List<Journal> allJournal = db.getAll();
		EntryDatabase newDb = ComponentProvider.getEntryDatabase(newDataSource);
		indexer.clear();
		newDb.clear();
		for (Journal i : allJournal) {
			i.setId(null);
			newDb.addEntry(i);
		}
		for (Journal i : newDb.getAll()) {
			indexer.add(tr.decryptJournal(i));
		}
		db.clear();
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, newDataSource);
	}

}
