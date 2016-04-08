package net.viperfish.journal.operation;

import java.util.HashMap;
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

/**
 * changes the configuration and refresh/re-add all entries to reflect the
 * change immediately
 * 
 * @author sdai
 *
 */
final class ChangeConfigurationOperation extends InjectedOperation {

	private Map<String, String> config;

	ChangeConfigurationOperation(Map<String, String> config) {
		this.config = new HashMap<>();
		this.config.putAll(config);
	}

	private void resetUnits() {
		db().clear();
		indexer().clear();
		auth().clear();
	}

	private Map<String, String> backUpConfig(Map<String, String> newConfig) {
		Map<String, String> old = new HashMap<>();
		for (Entry<String, String> i : newConfig.entrySet()) {
			old.put(i.getKey(), Configuration.getString(i.getKey()));
		}
		return old;
	}

	private void revert(List<Journal> all, String password, Map<String, String> old) {
		// reverts configuration
		for (Entry<String, String> i : old.entrySet()) {
			Configuration.setProperty(i.getKey(), i.getValue());
		}

		// refresh all providers to reflect changes in configuration
		EntryDatabases.INSTANCE.refreshAll();
		Indexers.INSTANCE.refreshAll();
		AuthManagers.INSTANCE.refreshAll();
		JournalTransformers.INSTANCE.refreshAll();
		// re-initialize components
		this.refresh();

		// clear new components
		resetUnits();
		// set the password on the new Auth manager
		auth().setPassword(password);

		// re-add all entries to reflect changes in the encryption
		// configuration, entry database, and indexer
		for (Journal i : all) {
			i.setId(null);
			db().addEntry(i);
			indexer().add(i);
		}
	}

	@Override
	public void execute() {

		Map<String, String> old = backUpConfig(config);

		// actually load all units
		this.refresh();

		// save all entries in memory
		List<Journal> result = db().getAll();

		// save the password in memory
		String password = auth().getPassword();

		try {

			// clear all entries
			resetUnits();

			// updates configuration
			for (Entry<String, String> i : config.entrySet()) {
				Configuration.setProperty(i.getKey(), i.getValue());
			}

			// refresh all providers to reflect changes in configuration
			EntryDatabases.INSTANCE.refreshAll();
			Indexers.INSTANCE.refreshAll();
			AuthManagers.INSTANCE.refreshAll();
			JournalTransformers.INSTANCE.refreshAll();

			// re-initialize components
			this.refresh();

			// clear new components
			resetUnits();

			// set the password on the new Auth manager
			auth().setPassword(password);

			// re-add all entries to reflect changes in the encryption
			// configuration, entry database, and indexer
			for (Journal i : result) {
				i.setId(null);
				db().addEntry(i);
				indexer().add(i);
			}
		} catch (RuntimeException e1) {
			revert(result, password, old);
			throw e1;
		} finally {

			// save the configuration
			try {
				Configuration.save();
			} catch (ConfigurationException e) {
				throw new RuntimeException(e);
			}
		}

	}

}
