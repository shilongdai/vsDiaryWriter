package net.viperfish.journal.persistent;

import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.index.Indexer;

public interface IndexerFactory {
	public Indexer<Journal> createIndexer();

	public void cleanUp();
}
