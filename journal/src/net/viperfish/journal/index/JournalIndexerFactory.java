package net.viperfish.journal.index;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.utils.index.Indexer;

public class JournalIndexerFactory implements IndexerFactory {

	private JournalIndexer indexer;

	public JournalIndexerFactory() {
		indexer = new JournalIndexer();
	}

	@Override
	public Indexer<Journal> createIndexer() {
		return indexer;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}
