package net.viperfish.journal.index;

import java.io.File;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.utils.index.Indexer;

public class JournalIndexerFactory implements IndexerFactory {

	private File dataDir;
	private JournalIndexer indexer;

	public JournalIndexerFactory() {
	}

	@Override
	public Indexer<Journal> createIndexer() {
		indexer = new JournalIndexer(dataDir);
		return indexer;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataDir(File dir) {
		dataDir = dir;

	}

	@Override
	public Indexer<Journal> getIndexer() {
		if (indexer == null) {
			indexer = new JournalIndexer(dataDir);
		}
		return indexer;
	}

}
