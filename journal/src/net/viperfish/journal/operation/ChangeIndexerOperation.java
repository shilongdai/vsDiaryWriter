package net.viperfish.journal.operation;

import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public class ChangeIndexerOperation implements Operation {

	private String toChange;
	private Indexer<Journal> indexer;
	private EntryDatabase db;
	private JournalTransformer tr;

	public ChangeIndexerOperation(String toChange) {
		this.toChange = toChange;
		indexer = ComponentProvider.getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		tr = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
	}

	@Override
	public void execute() {
		tr.setPassword(
				ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT)).getPassword());
		List<Journal> allJournal = db.getAll();
		List<Journal> decrypted = new LinkedList<>();
		for (Journal i : allJournal) {
			decrypted.add(tr.decryptJournal(i));
		}
		Indexer<Journal> change = ComponentProvider.getIndexer(toChange);
		for (Journal i : decrypted) {
			change.add(i);
		}
		indexer.clear();
		Configuration.setProperty(ConfigMapping.INDEX_PROVIDER, toChange);
	}

}
