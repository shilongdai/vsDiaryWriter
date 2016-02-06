package net.viperfish.journal.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public enum EntryDatabases {
	INSTANCE;
	private Map<String, Provider<EntryDatabase>> databaseProviders;
	private String defaultDatabaseProvider;
	private JournalEncryptionWrapper wrapper;
	private JournalTransformer tr;

	private EntryDatabases() {
		databaseProviders = new HashMap<>();
		defaultDatabaseProvider = "viperfish";
		wrapper = new JournalEncryptionWrapper();
	}

	private void initWrapper(EntryDatabase db) {
		if (db == null) {
			return;
		}
		String pass = AuthManagers.INSTANCE.getAuthManager().getPassword();
		if (tr == null) {
			tr = JournalTransformers.INSTANCE.getTransformer();
			tr.setPassword(pass);
			wrapper.setEncryptor(tr);
		}
		wrapper.setDb(db);
	}

	public void registerEntryDatabaseProvider(Provider<EntryDatabase> p) {
		databaseProviders.put(p.getName(), p);
	}

	public Map<String, Provider<EntryDatabase>> getDatabaseProviders() {
		return databaseProviders;
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
		}
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			EntryDatabase db = p.getValue().newInstance(instanceType);
			if (db != null) {
				result = db;
				break;
			}
		}
		initWrapper(result);
		wrapper.setDb(result);
		return wrapper;
	}

	public EntryDatabase newEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).getInstance(instanceType);
	}

	public EntryDatabase getEntryDatabase() {
		return getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
	}

	public EntryDatabase getEntryDatabase(String instanceType) {
		EntryDatabase result = null;
		EntryDatabase def = databaseProviders.get(defaultDatabaseProvider).getInstance(instanceType);
		if (def != null) {
			result = def;
		}
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			EntryDatabase db = p.getValue().getInstance(instanceType);
			if (db != null) {
				result = db;
				break;
			}
		}
		initWrapper(result);
		wrapper.setDb(result);
		return wrapper;
	}

	public EntryDatabase getEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).getInstance(instanceType);
	}

	public void dispose() {
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			p.getValue().dispose();
			System.err.println("disposed " + p.getKey());
		}
		databaseProviders.clear();
		System.err.println("disposed database providers");
	}
}
