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

public class ChangeJournalTransformerOperation implements Operation {

	private String toChange;
	private EntryDatabase db;
	private JournalTransformer tr;
	private Indexer<Journal> indexer;

	public ChangeJournalTransformerOperation(String newTrans) {
		this.toChange = newTrans;
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_PROVIDER));
		tr = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
		indexer = ComponentProvider.getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
	}

	@Override
	public void execute() {
		tr.setPassword(
				ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT)).getPassword());
		List<Journal> allJournal = new LinkedList<>();
		for (Journal i : db.getAll()) {
			i.setId(null);
			allJournal.add(tr.decryptJournal(i));
		}
		JournalTransformer newTr = ComponentProvider.getTransformer(toChange);
		db.clear();
		indexer.clear();
		for (Journal i : allJournal) {
			Journal tmp = db.addEntry(newTr.encryptJournal(i));
			indexer.add(tmp);
		}
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, toChange);
	}

}
