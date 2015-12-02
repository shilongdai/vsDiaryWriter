package net.viperfish.journal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.viperfish.journal.fileDatabase.GZippedDataSourceFactory;
import net.viperfish.journal.index.JournalIndexerFactory;
import net.viperfish.utils.config.ComponentConfig;

public class SystemConfig extends ComponentConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7978605296817709401L;

	public SystemConfig() {
		super("system");
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
		this.setProperty("DataSourceFactory",
				GZippedDataSourceFactory.class.getCanonicalName());
		this.setProperty("IndexerFactory",
				JournalIndexerFactory.class.getCanonicalName());

	}

	@Override
	public Map<String, String> getHelp() {
		// TODO Auto-generated method stub
		return null;
	}
}
