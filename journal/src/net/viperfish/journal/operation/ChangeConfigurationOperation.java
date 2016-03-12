package net.viperfish.journal.operation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;

import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformers;
import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.index.Indexer;

public class ChangeConfigurationOperation implements Operation {

	private Map<String, String> config;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private AuthenticationManager auth;

	private void initComponents() {
		auth = AuthManagers.INSTANCE.getAuthManager();
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();
	}

	public ChangeConfigurationOperation(Map<String, String> config) {
		this.config = config;
		initComponents();
	}

	private void resetUnits() {
		db.clear();
		indexer.clear();
		auth.clear();
	}

	@Override
	public void execute() {
		List<Journal> result = db.getAll();
		String password = auth.getPassword();
		resetUnits();
		for (Entry<String, String> i : config.entrySet()) {
			Configuration.setProperty(i.getKey(), i.getValue());
		}
		EntryDatabases.INSTANCE.refreshAll();
		Indexers.INSTANCE.refreshAll();
		AuthManagers.INSTANCE.refreshAll();
		JournalTransformers.INSTANCE.refreshAll();
		initComponents();
		resetUnits();
		auth.setPassword(password);
		for (Journal i : result) {
			i.setId(null);
			db.addEntry(i);
			indexer.add(i);
		}
		try {
			Configuration.save();
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}

	}

}
