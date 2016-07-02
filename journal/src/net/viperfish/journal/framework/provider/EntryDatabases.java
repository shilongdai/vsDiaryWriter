package net.viperfish.journal.framework.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;

/**
 * a provider manager for the EntryDatabase
 * 
 * @author sdai
 *
 */
public enum EntryDatabases {
	INSTANCE;
	private Map<String, DatabaseProvider> databaseProviders;
	private String defaultDatabaseProvider;
	private JournalEncryptionWrapper wrapper;

	private EntryDatabases() {
		databaseProviders = new HashMap<>();
		defaultDatabaseProvider = "viperfish";
	}

	private void initWrapper(EntryDatabase db) {
		if (db == null) {
			return;
		}
		if (wrapper == null) {
			wrapper = new JournalEncryptionWrapper();
			AuthManagers.INSTANCE.registerObserver(wrapper);
		}
		wrapper.setDb(db);
	}

	public void registerEntryDatabaseProvider(DatabaseProvider p) {
		databaseProviders.put(p.getName(), p);
		p.registerConfig();
	}

	public Map<String, DatabaseProvider> getDatabaseProviders() {
		return this.databaseProviders;
	}

	public String getDefaultDatabaseProvider() {
		return defaultDatabaseProvider;
	}

	public void setDefaultDatabaseProvider(String defaultDatabaseProvider) {
		this.defaultDatabaseProvider = defaultDatabaseProvider;
	}

	public EntryDatabase newEntryDatabase() {
		return newEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
	}

	public EntryDatabase newEntryDatabase(String instanceType) {
		EntryDatabase result = null;
		EntryDatabase def = databaseProviders.get(defaultDatabaseProvider).newInstance(instanceType);
		if (def != null) {
			result = def;
		} else {
			for (Entry<String, DatabaseProvider> p : databaseProviders.entrySet()) {
				EntryDatabase db = p.getValue().newInstance(instanceType);
				if (db != null) {
					result = db;
					break;
				}
			}
		}
		initWrapper(result);
		return wrapper;
	}

	public EntryDatabase newEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).newInstance(instanceType);
	}

	public EntryDatabase getEntryDatabase() {
		return getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
	}

	public EntryDatabase getEntryDatabase(String instanceType) {
		EntryDatabase result = null;
		EntryDatabase def = databaseProviders.get(defaultDatabaseProvider).getInstance(instanceType);
		if (def != null) {
			result = def;
		} else {
			for (Entry<String, DatabaseProvider> p : databaseProviders.entrySet()) {
				EntryDatabase db = p.getValue().getInstance(instanceType);
				if (db != null) {
					result = db;
					break;
				}
			}
		}
		initWrapper(result);
		return wrapper;
	}

	public EntryDatabase getEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).getInstance(instanceType);
	}

	public void dispose() {
		for (Entry<String, DatabaseProvider> p : databaseProviders.entrySet()) {
			p.getValue().dispose();
			System.err.println("disposed " + p.getKey());
		}
		databaseProviders.clear();
		System.err.println("disposed database providers");
	}

	void refreshAdapter() {
		wrapper = null;
	}

	public void refreshAll() {
		for (Entry<String, DatabaseProvider> p : databaseProviders.entrySet()) {
			p.getValue().refresh();
		}
	}
}
