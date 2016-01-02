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

import net.viperfish.journal.framework.AuthenticationManager;

public class LoginPrompt {

	protected Shell shell;
	private Text text;
	private AuthenticationManager auth;
	private String password;

	public LoginPrompt(AuthenticationManager auth) {
		this.auth = auth;
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public String open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return password;
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(SWT.SYSTEM_MODAL | SWT.TITLE | SWT.BORDER);
		shell.setSize(450, 156);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Password");

		text = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);

		final Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setText("Password mismatch");
		lblNewLabel_1.setVisible(false);

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				lblNewLabel_1.setVisible(false);
			}
		});

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setText("Enter");
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				String toTest = text.getText();
				if (!auth.isPasswordSet()) {
					auth.setPassword(toTest);
					password = toTest;
					shell.dispose();
				} else {
					boolean correct = auth.verify(toTest);
					if (correct) {
						password = text.getText();
						shell.dispose();
					} else {
						lblNewLabel_1.setVisible(true);
					}
				}
			}

		});

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setText("Quit");
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				System.exit(1);
			}

		});

		Label lblNewLabel_2 = new Label(shell, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel_2.setText("If this is the first run, then the password you typed is your password");

	}

}
