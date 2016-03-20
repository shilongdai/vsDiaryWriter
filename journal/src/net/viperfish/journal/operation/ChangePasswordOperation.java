package net.viperfish.journal.operation;

import java.util.List;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.utils.index.Indexer;

public class ChangePasswordOperation implements Operation {

	private EntryDatabase db;
	private AuthenticationManager auth;
	private Indexer<Journal> indexer;
	private String pass;

	public ChangePasswordOperation(String password) {
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		auth = AuthManagers.INSTANCE.getAuthManager();
		indexer = Indexers.INSTANCE.getIndexer();
		this.pass = password;
	}

	@Override
	public void execute() {
		indexer.clear();
		List<Journal> buffer = db.getAll();
		db.clear();
		auth.setPassword(pass);
		for (Journal i : buffer) {
			i.setId(null);
			Journal added = db.addEntry(i);
			indexer.add(added);
		}
	}

}
