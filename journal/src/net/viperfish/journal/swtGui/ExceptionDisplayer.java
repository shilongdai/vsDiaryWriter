package net.viperfish.journal.swtGui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.viperfish.journal.framework.Observer;

public class ExceptionDisplayer implements Observer<Throwable> {

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
	public void beNotified(Throwable data) {
		displayException(data);
	}

	public ExceptionDisplayer(Shell s) {
	}

	private void displayException(final Throwable e) {
		System.err.println("handler called");
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				errorDialogWithStackTrace("Exception Occured", e);
			}
		});
	}
}
