package net.viperfish.journal.indexProvider;

import java.io.File;

import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.index.Indexer;

public interface IndexerFactory {
	public Indexer<Journal> createIndexer();

	public Indexer<Journal> getIndexer();

	public void cleanUp();

	public void setDataDir(File dir);
}
