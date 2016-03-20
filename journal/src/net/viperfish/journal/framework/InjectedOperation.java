package net.viperfish.journal.framework;

import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.utils.index.Indexer;

public abstract class InjectedOperation implements Operation {

	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private AuthenticationManager auth;

	protected EntryDatabase db() {
		if (db == null) {
			db = EntryDatabases.INSTANCE.getEntryDatabase();
		}
		return db;
	}

	protected Indexer<Journal> indexer() {
		if (indexer == null) {
			indexer = Indexers.INSTANCE.getIndexer();
		}
		return indexer;
	}

	protected AuthenticationManager auth() {
		if (auth == null) {
			auth = AuthManagers.INSTANCE.getAuthManager();
		}
		return auth;
	}

	protected void refresh() {
		auth = AuthManagers.INSTANCE.getAuthManager();
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

}
