package net.viperfish.journal.swtGui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.viperfish.journal.framework.Observer;
import net.viperfish.journal.framework.errors.ChangeConfigurationFailException;
import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.CompromisedDataException;
import net.viperfish.journal.framework.errors.FailToExportEntriesException;
import net.viperfish.journal.framework.errors.FailToImportEntriesException;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.journal.framework.errors.OperationErrorException;

public class ExceptionDisplayer implements Observer<Exception> {

	public static void errorDialogWithStackTrace(String msg, Throwable t) {

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);

		final String trace = sw.toString(); // stack trace as a string

		// Temp holder of child statuses
		List<Status> childStatuses = new ArrayList<>();

		// Split output by OS-independend new-line
		for (String line : trace.split(System.getProperty("line.separator"))) {
			// build & add status
			childStatuses.add(new Status(IStatus.ERROR, "Journal", line));
		}

		MultiStatus ms = new MultiStatus("Journal", IStatus.ERROR, childStatuses.toArray(new Status[] {}), // convert
																											// to
																											// array
																											// of
																											// statuses
				t.getLocalizedMessage(), t);

		ErrorDialog.openError(null, "Error", msg, ms);
	}

	@Override
	public void beNotified(Exception data) {
		displayException(data);
	}

	public ExceptionDisplayer(Shell s) {
	}

	private void displayException(final Exception e) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				try {
					if (e instanceof OperationErrorException) {
						throw e;
					}
					Throwable t = e.getCause();
					if (t != null) {
						throw e.getCause();
					} else {
						throw e;
					}
				} catch (CompromisedDataException e1) {
					MessageDialog.openWarning(null, "Security Compromise", "Your entries are corrupted or compromised");
				} catch (FailToLoadCredentialException e1) {
					MessageDialog.openError(null, "Critical Error",
							"Failed to load credential information from file, please ensure that the program has read permission to the current or the home directory and the filesystem is not corrupted");
				} catch (FailToStoreCredentialException e1) {
					MessageDialog.openError(null, "Critical Error",
							"Failed to write credential information to file, please ensure that the program can write to the current directory or the home directory");
				} catch (FailToSyncEntryException e1) {
					MessageDialog.openError(null, "Error On Save", "Failed to save entry:" + e1.getMessage());
				} catch (FailToSyncCipherDataException e1) {
					MessageDialog.openError(null, "Error With Transformer",
							"Cannot synchronize encryption data:" + e1.getMessage());
				} catch (CipherException e1) {
					MessageDialog.openError(null, "Error with encryption",
							"Cannot initialize encryption, please check configuration");
				} catch (FailToImportEntriesException e1) {
					MessageDialog.openError(null, "Error import", "Cannot import entries");
				} catch (FailToExportEntriesException e1) {
					MessageDialog.openError(null, "Cannot Export", "Cannot export entries");
				} catch (ChangeConfigurationFailException e1) {
					MessageDialog.openError(null, "Change Failed",
							"Failed to change configuration. Please check the condition of the modules. Message:"
									+ e1.getMessage());
				} catch (OperationErrorException e1) {
					MessageDialog.openError(null, "Operation Error", e1.getMessage());
				} catch (Throwable e1) {
					errorDialogWithStackTrace("Error", e1);
				}

			}
		});
	}
}
