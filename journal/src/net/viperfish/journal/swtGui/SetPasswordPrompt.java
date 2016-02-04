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

import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.AuthenticationManager;

public class SetPasswordPrompt {

	private AuthenticationManager mgmt;
	private Text newPassword;
	private Text retypePassword;
	private boolean result;

	public SetPasswordPrompt() {
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
		mgmt = AuthManagers.INSTANCE.getAuthManager();
		Display display = Display.getDefault();
		final Shell shell = new Shell();
		shell.setSize(450, 224);
		shell.setText("Set Password");
		shell.setLayout(new GridLayout(2, false));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label newPassLabel = new Label(shell, SWT.NONE);
		newPassLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		newPassLabel.setText("New Password");

		newPassword = new Text(shell, SWT.PASSWORD);
		newPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label retypeLabel = new Label(shell, SWT.NONE);
		retypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		retypeLabel.setText("Retype");

		retypePassword = new Text(shell, SWT.PASSWORD);
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

		Button done = new Button(shell, SWT.NONE);
		done.setText("Done");

		done.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (canSavePassword()) {
					mgmt.setPassword(newPassword.getText());
					result = true;
					shell.dispose();
				}
			}

		});

		retypePassword.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				if (!canSavePassword()) {
					passwordMismatch.setVisible(true);

				} else {
					passwordMismatch.setVisible(false);
				}

			}
		});

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
