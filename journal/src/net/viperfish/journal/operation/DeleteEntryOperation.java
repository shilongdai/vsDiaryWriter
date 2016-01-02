package net.viperfish.journal.operation;

import net.viperfish.journal.ConfigMapping;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.provider.ComponentProvider;
import net.viperfish.utils.index.Indexer;

public class DeleteEntryOperation implements Operation {
	private Long id;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	public DeleteEntryOperation(Long i) {
		this.id = i;
		db = ComponentProvider
				.getEntryDatabase(JournalApplication.getConfiguration().getString(ConfigMapping.DB_COMPONENT));
		indexer = ComponentProvider
				.getIndexer(JournalApplication.getConfiguration().getString(ConfigMapping.INDEXER_COMPONENT));
	}

	@Override
	public void execute() {
		db.removeEntry(id);
		indexer.delete(id);
	}

}
