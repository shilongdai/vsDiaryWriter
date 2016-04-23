package net.viperfish.journal.operation;

import java.io.File;
import java.util.List;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

/**
 * changes the password and re-encrypt entries with the new key
 * 
 * @author sdai
 *
 */
final class ChangePasswordOperation extends InjectedOperation {

	private String pass;

	ChangePasswordOperation(String password) {
		this.pass = password;
	}

	@Override
	public void execute() {
		List<Journal> buffer = db().getAll();
		// set the new password
		try {
			auth().setPassword(pass);

			// clear all
			indexer().clear();
			db().clear();

			// re-encrypt all entries
			for (Journal i : buffer) {
				i.setId(null);
				Journal added = db().addEntry(i);
				indexer().add(added);
			}
		} catch (FailToStoreCredentialException e) {
			File userHome = new File(System.getProperty("user.home"));
			File export = new File(userHome, "export.txt");
			OperationErrorException err = new OperationErrorException("Failed to save the new password:"
					+ e.getMessage() + " Exporting all entries to " + export.getAbsolutePath());
			new ExportJournalOperation(export.getAbsolutePath()).execute();
			throw err;
		} catch (FailToSyncEntryException e) {
			File userHome = new File(System.getProperty("user.home"));
			File export = new File(userHome, "export.txt");
			OperationErrorException err = new OperationErrorException("Failed to re-add entries with the new password:"
					+ e.getMessage() + " Exporting all entries to " + export.getAbsolutePath());
			new ExportJournalOperation(export.getAbsolutePath()).execute();
			throw err;
		}

	}

}
