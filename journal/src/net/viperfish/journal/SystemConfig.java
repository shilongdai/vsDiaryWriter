package net.viperfish.journal;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.reflections.Reflections;

import net.viperfish.journal.dbDatabase.H2DatasourceFactory;
import net.viperfish.journal.index.JournalIndexerFactory;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.SecureFactoryWrapper;
import net.viperfish.utils.config.ComponentConfig;

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
		Set<Class<? extends DataSourceFactory>> result = new Reflections("net.viperfish.journal")
				.getSubTypesOf(DataSourceFactory.class);
		result.remove(SecureFactoryWrapper.class);
		for (Class<? extends DataSourceFactory> i : result) {
			available.add(i.getCanonicalName());
		}
		return available;
	}

	private Set<String> availableIF() {
		Set<String> available = new TreeSet<>();
		Set<Class<? extends IndexerFactory>> result = new Reflections("net.viperfish.journal")
				.getSubTypesOf(IndexerFactory.class);
		for (Class<? extends IndexerFactory> i : result) {
			available.add(i.getCanonicalName());
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
		result.add("DataSourceFactory");
		result.add("IndexerFactory");
		return result;
	}

	@Override
	public void fillInDefault() {
		this.setProperty("DataSourceFactory", H2DatasourceFactory.class.getCanonicalName());
		this.setProperty("IndexerFactory", JournalIndexerFactory.class.getCanonicalName());

	}

	@Override
	public Set<String> getOptions(String key) {
		if (key.equals("DataSourceFactory")) {
			return this.availableDF();
		}

		if (key.equals("IndexerFactory")) {
			return this.availableIF();
		}
		return new TreeSet<>();
	}

	@Override
	public int hashCode() {
		return getUnitName().hashCode();
	}
}
