package net.viperfish.journal.provider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.utils.index.Indexer;

public class ComponentProvider {
	private ComponentProvider() {

	}

	private static Map<String, Provider<EntryDatabase>> databaseProviders;
	private static Map<String, Provider<Indexer<Journal>>> indexerProviders;
	private static Map<String, Provider<JournalTransformer>> secureProviders;
	private static Map<String, Provider<AuthenticationManager>> authProviders;
	private static String defaultDatabaseProvider;
	private static String defaultIndexerProvider;
	private static String defaultTransformerProvider;
	private static String defaultAuthProvider;
	private static ModuleLoader loader;

	static {
		databaseProviders = new HashMap<>();
		indexerProviders = new HashMap<>();
		secureProviders = new HashMap<>();
		authProviders = new HashMap<>();
	}

	public static ModuleLoader getLoader() {
		return loader;
	}

	public static void setLoader(ModuleLoader loader) {
		ComponentProvider.loader = loader;
	}

	public static void loadDatabaseProvider(File baseDir) {
		for (Provider<EntryDatabase> i : loader.loadDatabaseProvider(baseDir)) {
			registerEntryDatabaseProvider(i);
		}
	}

	public static void loadAuthProvider(File baseDir) {
		for (Provider<AuthenticationManager> i : loader.loadAuthProvider(baseDir)) {
			registerAuthProvider(i);
		}
	}

	public static void loadTransformerProvider(File baseDir) {
		for (Provider<JournalTransformer> i : loader.loadTransformerProvider(baseDir)) {
			registerTransformerProvider(i);
		}
	}

	public static void loadIndexer(File baseDir) {
		for (Provider<Indexer<Journal>> i : loader.loadIndexer(baseDir)) {
			registerIndexerProvider(i);
		}
	}

	public static void registerEntryDatabaseProvider(Provider<EntryDatabase> p) {
		databaseProviders.put(p.getName(), p);
	}

	public static void registerIndexerProvider(Provider<Indexer<Journal>> p) {
		indexerProviders.put(p.getName(), p);
	}

	public static void registerTransformerProvider(Provider<JournalTransformer> p) {
		secureProviders.put(p.getName(), p);
	}

	public static void registerAuthProvider(Provider<AuthenticationManager> p) {
		authProviders.put(p.getName(), p);
	}

	public static Map<String, Provider<EntryDatabase>> getDatabaseProviders() {
		return databaseProviders;
	}

	public static Map<String, Provider<Indexer<Journal>>> getIndexerProviders() {
		return indexerProviders;
	}

	public static Map<String, Provider<JournalTransformer>> getSecureProviders() {
		return secureProviders;
	}

	public static Map<String, Provider<AuthenticationManager>> getAuthProviders() {
		return authProviders;
	}

	public static String getDefaultDatabaseProvider() {
		return defaultDatabaseProvider;
	}

	public static void setDefaultDatabaseProvider(String defaultDatabaseProvider) {
		ComponentProvider.defaultDatabaseProvider = defaultDatabaseProvider;
	}

	public static String getDefaultIndexerProvider() {
		return defaultIndexerProvider;
	}

	public static void setDefaultIndexerProvider(String defaultIndexerProvider) {
		ComponentProvider.defaultIndexerProvider = defaultIndexerProvider;
	}

	public static String getDefaultTransformerProvider() {
		return defaultTransformerProvider;
	}

	public static void setDefaultTransformerProvider(String defaultTransformerProvider) {
		ComponentProvider.defaultTransformerProvider = defaultTransformerProvider;
	}

	public static String getDefaultAuthProvider() {
		return defaultAuthProvider;
	}

	public static void setDefaultAuthProvider(String defaultAuthProvider) {
		ComponentProvider.defaultAuthProvider = defaultAuthProvider;
	}

	public static EntryDatabase newEntryDatabase() {
		return databaseProviders.get(defaultDatabaseProvider).newInstance();
	}

	public static EntryDatabase newEntryDatabase(String instanceType) {
		EntryDatabase def = databaseProviders.get(defaultDatabaseProvider).newInstance(instanceType);
		if (def != null) {
			return def;
		}
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			EntryDatabase db = p.getValue().newInstance(instanceType);
			if (db != null) {
				return db;
			}
		}
		return null;
	}

	public static EntryDatabase newEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).getInstance(instanceType);
	}

	public static EntryDatabase getEntryDatabase() {
		return databaseProviders.get(defaultDatabaseProvider).getInstance();
	}

	public static EntryDatabase getEntryDatabase(String instanceType) {
		EntryDatabase def = databaseProviders.get(defaultDatabaseProvider).getInstance(instanceType);
		if (def != null) {
			return def;
		}
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			EntryDatabase db = p.getValue().getInstance(instanceType);
			if (db != null) {
				return db;
			}
		}
		return null;
	}

	public static EntryDatabase getEntryDatabase(String provider, String instanceType) {
		return databaseProviders.get(provider).getInstance(instanceType);
	}

	public Indexer<Journal> newIndexer() {
		return indexerProviders.get(defaultIndexerProvider).newInstance();
	}

	public static Indexer<Journal> newIndexer(String instance) {
		Indexer<Journal> ind = indexerProviders.get(defaultIndexerProvider).newInstance(instance);
		if (ind != null) {
			return ind;
		}
		for (Entry<String, Provider<Indexer<Journal>>> j : indexerProviders.entrySet()) {
			Indexer<Journal> indexer = j.getValue().newInstance(instance);
			if (indexer != null) {
				return indexer;
			}
		}
		return null;
	}

	public static Indexer<Journal> newIndexer(String provider, String instance) {
		return indexerProviders.get(provider).newInstance(instance);
	}

	public static Indexer<Journal> getIndexer() {
		return indexerProviders.get(defaultIndexerProvider).getInstance();
	}

	public static Indexer<Journal> getIndexer(String instance) {
		Indexer<Journal> ind = indexerProviders.get(defaultIndexerProvider).getInstance(instance);
		if (ind != null) {
			return ind;
		}
		for (Entry<String, Provider<Indexer<Journal>>> j : indexerProviders.entrySet()) {
			Indexer<Journal> indexer = j.getValue().getInstance(instance);
			if (indexer != null) {
				return indexer;
			}
		}
		return null;
	}

	public static Indexer<Journal> getIndexer(String provider, String instance) {
		return indexerProviders.get(provider).getInstance(instance);
	}

	public static JournalTransformer newTransformer() {
		return secureProviders.get(defaultTransformerProvider).newInstance();
	}

	public static JournalTransformer newTransformer(String instance) {
		JournalTransformer t = secureProviders.get(defaultTransformerProvider).newInstance(instance);
		if (t != null) {
			return t;
		}
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			JournalTransformer tmp = iter.getValue().newInstance(instance);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	public static JournalTransformer newTransformer(String provider, String instance) {
		return secureProviders.get(provider).newInstance(instance);
	}

	public static JournalTransformer getTransformer() {
		return secureProviders.get(defaultTransformerProvider).getInstance();
	}

	public static JournalTransformer getTransformer(String instance) {
		JournalTransformer t = secureProviders.get(defaultTransformerProvider).getInstance(instance);
		if (t != null) {
			return t;
		}
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			JournalTransformer tmp = iter.getValue().getInstance(instance);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	public static JournalTransformer getTransformer(String provider, String instance) {
		return secureProviders.get(provider).getInstance(instance);
	}

	public static AuthenticationManager newAuthManager() {
		return authProviders.get(defaultAuthProvider).newInstance();
	}

	public static AuthenticationManager newAuthManager(String instance) {
		AuthenticationManager am = authProviders.get(defaultAuthProvider).newInstance();
		if (am != null) {
			return am;
		}
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			AuthenticationManager result = i.getValue().newInstance(instance);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public static AuthenticationManager newAuthManager(String provider, String instance) {
		return authProviders.get(provider).newInstance(instance);
	}

	public static AuthenticationManager getAuthManager() {
		return authProviders.get(defaultAuthProvider).getInstance();
	}

	public static AuthenticationManager getAuthManager(String instance) {
		AuthenticationManager am = authProviders.get(defaultAuthProvider).getInstance();
		if (am != null) {
			return am;
		}
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			AuthenticationManager result = i.getValue().getInstance(instance);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	public static AuthenticationManager getAuthManager(String provider, String instance) {
		return authProviders.get(provider).getInstance(instance);
	}

	public static void dispose() {
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			i.getValue().dispose();
			System.err.println("disposed " + i.getKey());
		}
		authProviders.clear();
		System.err.println("disposed auth providers");
		for (Entry<String, Provider<JournalTransformer>> iter : secureProviders.entrySet()) {
			iter.getValue().dispose();
			System.err.println("disposed " + iter.getKey());
		}
		secureProviders.clear();
		System.err.println("disposed secure providers");
		for (Entry<String, Provider<Indexer<Journal>>> j : indexerProviders.entrySet()) {
			j.getValue().dispose();
			System.err.println("disposed " + j.getKey());
		}
		indexerProviders.clear();
		System.err.println("disposed indexer providers");
		for (Entry<String, Provider<EntryDatabase>> p : databaseProviders.entrySet()) {
			p.getValue().dispose();
			System.err.println("disposed " + p.getKey());
		}
		System.err.println("disposed database providers");
	}
}
