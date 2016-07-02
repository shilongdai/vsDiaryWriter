package net.viperfish.journal;

import java.io.File;
import java.util.List;

import net.viperfish.journal.framework.ModuleLoader;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.AuthProvider;
import net.viperfish.journal.framework.provider.DatabaseProvider;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.IndexerProvider;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.journal.framework.provider.JournalTransformers;
import net.viperfish.journal.framework.provider.TransformerProvider;
import ro.fortsoft.pf4j.DefaultPluginManager;
import ro.fortsoft.pf4j.PluginManager;

public class PF4JModuleLoader implements ModuleLoader {

	public PF4JModuleLoader() {

	}

	private void loadDBModules(PluginManager manager) {
		List<DatabaseProvider> dbs = manager.getExtensions(DatabaseProvider.class);
		for (DatabaseProvider i : dbs) {
			EntryDatabases.INSTANCE.registerEntryDatabaseProvider(i);
		}
	}

	private void loadIndexerModules(PluginManager manager) {
		List<IndexerProvider> indexers = manager.getExtensions(IndexerProvider.class);
		for (IndexerProvider i : indexers) {
			Indexers.INSTANCE.registerIndexerProvider(i);
		}
	}

	private void loadAuthentiationModules(PluginManager manager) {
		List<AuthProvider> auths = manager.getExtensions(AuthProvider.class);
		for (AuthProvider i : auths) {
			AuthManagers.INSTANCE.registerAuthProvider(i);
		}
	}

	private void loadEncryptors(PluginManager manager) {
		List<TransformerProvider> trans = manager.getExtensions(TransformerProvider.class);
		for (TransformerProvider i : trans) {
			JournalTransformers.INSTANCE.registerTransformerProvider(i);
		}
	}

	@Override
	public void loadModules(File baseDir) {
		PluginManager manager = new DefaultPluginManager(baseDir);
		manager.loadPlugins();
		manager.startPlugins();

		loadDBModules(manager);
		loadIndexerModules(manager);
		loadAuthentiationModules(manager);
		loadEncryptors(manager);
	}

}
