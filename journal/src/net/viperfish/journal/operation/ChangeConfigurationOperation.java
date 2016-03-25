package net.viperfish.journal.operation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.journal.framework.provider.JournalTransformers;

class ChangeConfigurationOperation extends InjectedOperation {

	private Map<String, String> config;

	public ChangeConfigurationOperation(Map<String, String> config) {
		this.config = config;
	}

	private void resetUnits() {
		db().clear();
		indexer().clear();
		auth().clear();
	}

	@Override
	public void execute() {
		this.refresh();
		List<Journal> result = db().getAll();
		String password = auth().getPassword();
		resetUnits();
		for (Entry<String, String> i : config.entrySet()) {
			Configuration.setProperty(i.getKey(), i.getValue());
		}
		EntryDatabases.INSTANCE.refreshAll();
		Indexers.INSTANCE.refreshAll();
		AuthManagers.INSTANCE.refreshAll();
		JournalTransformers.INSTANCE.refreshAll();
		this.refresh();
		resetUnits();
		auth().setPassword(password);
		for (Journal i : result) {
			i.setId(null);
			db().addEntry(i);
			indexer().add(i);
		}
		try {
			Configuration.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}

	}

}
