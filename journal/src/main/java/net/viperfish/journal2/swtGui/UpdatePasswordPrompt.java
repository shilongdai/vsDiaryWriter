package net.viperfish.journal2.swtGui;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
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

import net.viperfish.journal2.core.JournalI18NBundle;
import net.viperfish.journal2.error.FailToStoreCredentialException;

abstract class UpdatePasswordPrompt {
	private Text newPassword;
	private Text retypePassword;
	private boolean result;
	private Locale defaultLocale;
	private Logger logger = LogManager.getLogger();

	protected abstract void doUpdate(String password) throws FailToStoreCredentialException;

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
		// shell.setImage(SWTResourceManager.getImage(UpdatePasswordPrompt.class,
		// "/logo.ico"));
		shell.setSize(500, 250);
		shell.setText(JournalI18NBundle.getString("label.setPassword"));
		shell.setLayout(new GridLayout(2, false));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label newPassLabel = new Label(shell, SWT.NONE);
		newPassLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		newPassLabel.setText(JournalI18NBundle.getString("label.newPassword"));

		newPassword = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		newPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		Label retypeLabel = new Label(shell, SWT.NONE);
		retypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		retypeLabel.setText(JournalI18NBundle.getString("label.retypePassword"));

		retypePassword = new Text(shell, SWT.PASSWORD | SWT.BORDER);
		retypePassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);

		final Label passwordMismatch = new Label(shell, SWT.NONE);
		passwordMismatch.setText(JournalI18NBundle.getString("label.passwordMismatch"));
		passwordMismatch.setVisible(false);

		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText(JournalI18NBundle.getString("label.cancel"));

		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				result = false;
				shell.dispose();
			}

		});

		final Button done = new Button(shell, SWT.NONE);
		done.setText(JournalI18NBundle.getString("label.done"));

		done.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (canSavePassword()) {
					try {
						doUpdate(newPassword.getText());
					} catch (FailToStoreCredentialException e) {
						MessageDialog.openError(shell, JournalI18NBundle.getString("label.error"),
								JournalI18NBundle.getString("journal2.error.generic"));
						logger.error("Cannot persist password information", e);
					}
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
