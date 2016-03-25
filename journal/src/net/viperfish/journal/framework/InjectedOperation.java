package net.viperfish.journal.framework;

import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.utils.index.Indexer;

/**
 * this operation contains an EntryDatabase, an Indexer, and an Authentication
 * Manager ready to be used
 * 
 * @author sdai
 *
 */
public abstract class InjectedOperation implements Operation {

	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private AuthenticationManager auth;

	/**
	 * gets the embedded EntryDatabase
	 * 
	 * @return EntryDatabase
	 */
	protected EntryDatabase db() {
		if (db == null) {
			db = EntryDatabases.INSTANCE.getEntryDatabase();
		}
		return db;
	}

	/**
	 * gets the embedded Indexer
	 * 
	 * @return Indexer
	 */
	protected Indexer<Journal> indexer() {
		if (indexer == null) {
			indexer = Indexers.INSTANCE.getIndexer();
		}
		return indexer;
	}

	/**
	 * gets the embedded Authentication Manager
	 * 
	 * @return the authentication manager
	 */
	protected AuthenticationManager auth() {
		if (auth == null) {
			auth = AuthManagers.INSTANCE.getAuthManager();
		}
		return auth;
	}

	/**
	 * refresh components in the case of a configuration change
	 */
	protected void refresh() {
		auth = AuthManagers.INSTANCE.getAuthManager();
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

}
