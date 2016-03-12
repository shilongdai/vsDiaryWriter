package net.viperfish.journal.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.utils.index.Indexer;

/**
 * a provider manager for indexers
 * 
 * @author sdai
 *
 */
public enum Indexers {
	INSTANCE;
	private Map<String, Provider<Indexer<Journal>>> indexerProviders;
	private String defaultIndexerProvider;

	private Indexers() {
		indexerProviders = new HashMap<>();
		defaultIndexerProvider = "viperfish";
	}

	public void registerIndexerProvider(Provider<Indexer<Journal>> p) {
		indexerProviders.put(p.getName(), p);
		if (p.getConfigPages() != null) {
			ConfigPages.registerConfig(p.getConfigPages());
		}
	}

	public Map<String, Provider<Indexer<Journal>>> getIndexerProviders() {
		return indexerProviders;
	}

	public String getDefaultIndexerProvider() {
		return defaultIndexerProvider;
	}

	public void setDefaultIndexerProvider(String defaultIndexerProvider) {
		this.defaultIndexerProvider = defaultIndexerProvider;
	}

	public Indexer<Journal> newIndexer() {
		return newIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
	}

	public Indexer<Journal> newIndexer(String instance) {
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

	public Indexer<Journal> newIndexer(String provider, String instance) {
		return indexerProviders.get(provider).newInstance(instance);
	}

	public Indexer<Journal> getIndexer() {
		return getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
	}

	public Indexer<Journal> getIndexer(String instance) {
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

	public Indexer<Journal> getIndexer(String provider, String instance) {
		return indexerProviders.get(provider).getInstance(instance);
	}

	public void dispose() {
		for (Entry<String, Provider<Indexer<Journal>>> j : indexerProviders.entrySet()) {
			j.getValue().dispose();
			System.err.println("disposed " + j.getKey());
		}
		indexerProviders.clear();
		System.err.println("disposed indexer providers");
	}

	public void refreshAll() {
		for (Entry<String, Provider<Indexer<Journal>>> j : indexerProviders.entrySet()) {
			j.getValue().refresh();
		}
	}

}
