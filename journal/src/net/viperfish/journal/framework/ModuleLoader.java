package net.viperfish.journal.framework;

import java.io.File;
import java.util.Collection;

import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.utils.index.Indexer;

public interface ModuleLoader {

	public Collection<Provider<EntryDatabase>> loadDatabaseProvider(File baseDir);

	public Collection<Provider<AuthenticationManager>> loadAuthProvider(File baseDir);

	public Collection<Provider<JournalTransformer>> loadTransformerProvider(File baseDir);

	public Collection<Provider<Indexer<Journal>>> loadIndexer(File baseDir);

}
