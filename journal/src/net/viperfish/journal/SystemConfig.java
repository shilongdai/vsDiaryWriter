package net.viperfish.journal;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Provider;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.config.ComponentConfig;
import net.viperfish.utils.index.Indexer;;

public class SystemConfig extends ComponentConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7978605296817709401L;

	public SystemConfig() {
		super("system");
	}

	private Set<String> availableDF() {
		Set<String> available = new TreeSet<>();
		for (Entry<String, Provider<EntryDatabase>> i : ComponentProvider.getDatabaseProviders().entrySet()) {
			for (String iter : i.getValue().getSupported()) {
				available.add(iter);
			}
		}
		return available;
	}

	private Set<String> availableIF() {
		Set<String> available = new TreeSet<>();
		for (Entry<String, Provider<Indexer<Journal>>> i : ComponentProvider.getIndexerProviders().entrySet()) {
			for (String iter : i.getValue().getSupported()) {
				available.add(iter);
			}
		}
		return available;
	}

	@Override
	public Set<String> requiredConfig() {
		return new HashSet<String>();
	}

	@Override
	public Set<String> optionalConfig() {
		HashSet<String> result = new HashSet<String>();
		result.add("DataStorage");
		result.add("Indexer");
		return result;
	}

	@Override
	public void fillInDefault() {
		this.setProperty("DataStorage", "H2Database");
		this.setProperty("Indexer", "LuceneIndexer");

	}

	@Override
	public Set<String> getOptions(String key) {
		if (key.equals("DataStorage")) {
			return this.availableDF();
		}

		if (key.equals("Indexer")) {
			return this.availableIF();
		}
		return new TreeSet<>();
	}

	@Override
	public int hashCode() {
		return getUnitName().hashCode();
	}
}
