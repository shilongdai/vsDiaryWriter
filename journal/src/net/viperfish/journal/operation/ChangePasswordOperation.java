package net.viperfish.journal.operation;

import java.util.List;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;

class ChangePasswordOperation extends InjectedOperation {

	private String pass;

	public ChangePasswordOperation(String password) {
		this.pass = password;
	}

	@Override
	public void execute() {
		indexer().clear();
		List<Journal> buffer = db().getAll();
		db().clear();
		auth().setPassword(pass);
		for (Journal i : buffer) {
			i.setId(null);
			Journal added = db().addEntry(i);
			indexer().add(added);
		}
	}

}
