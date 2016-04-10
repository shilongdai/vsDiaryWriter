package net.viperfish.journal.swtGui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

abstract class UpdatePasswordPrompt {
	private Text newPassword;
	private Text retypePassword;
	private boolean result;

	protected abstract void doUpdate(String password);

	public UpdatePasswordPrompt() {
	}

	private boolean canSavePassword() {
		return newPassword.getText().equals(retypePassword.getText());
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public boolean open() {
		Display display = Display.getDefault();
		final Shell shell = new Shell(SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER);
		shell.setImage(SWTResourceManager.getImage(UpdatePasswordPrompt.class, "/logo.ico"));
		shell.setSize(500, 250);
		shell.setText("Set Password");
		shell.setLayout(new GridLayout(2, false));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label newPassLabel = new Label(shell, SWT.NONE);
		newPassLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		newPassLabel.setText("New Password");

		newPassword = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		newPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label retypeLabel = new Label(shell, SWT.NONE);
		retypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		retypeLabel.setText("Retype");

		retypePassword = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		retypePassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);

		final Label passwordMismatch = new Label(shell, SWT.NONE);
		passwordMismatch.setText("Password Mismatch");
		passwordMismatch.setVisible(false);

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");

		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = false;
				shell.dispose();
			}

		});

		final Button done = new Button(shell, SWT.NONE);
		done.setText("Done");

		done.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (canSavePassword()) {
					doUpdate(newPassword.getText());
					result = true;
					shell.dispose();
				} else {
					passwordMismatch.setVisible(true);
				}
			}

		});

		retypePassword.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!canSavePassword()) {
					passwordMismatch.setVisible(true);
					done.setEnabled(false);

				} else {
					passwordMismatch.setVisible(false);
					done.setEnabled(true);
				}

			}
		});

		shell.setDefaultButton(done);
		done.setEnabled(false);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
}
