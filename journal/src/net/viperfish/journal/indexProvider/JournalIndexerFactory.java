package net.viperfish.journal.indexProvider;

import java.io.File;

import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.index.Indexer;

public class JournalIndexerFactory implements IndexerFactory {

	private JournalIndexer indexer;

	public JournalIndexerFactory() {
	}

	@Override
	public Indexer<Journal> createIndexer() {
		indexer = new JournalIndexer();
		return indexer;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataDir(File dir) {

	}

	@Override
	public Indexer<Journal> getIndexer() {
		if (indexer == null) {
			indexer = new JournalIndexer();
		}
		return indexer;
	}

}
