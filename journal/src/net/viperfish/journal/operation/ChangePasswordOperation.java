package net.viperfish.journal.operation;

import java.util.List;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;

/**
 * changes the password and re-encrypt entries with the new key
 * 
 * @author sdai
 *
 */
class ChangePasswordOperation extends InjectedOperation {

	private String pass;

	public ChangePasswordOperation(String password) {
		this.pass = password;
	}

	@Override
	public void execute() {
		// save all entries in memory and clear all
		indexer().clear();
		List<Journal> buffer = db().getAll();
		db().clear();

		// set the new password
		auth().setPassword(pass);

		// re-encrypt all entries
		for (Journal i : buffer) {
			i.setId(null);
			Journal added = db().addEntry(i);
			indexer().add(added);
		}
	}

}
