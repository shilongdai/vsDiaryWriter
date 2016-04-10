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

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.provider.AuthManagers;

public class LoginPrompt {

	protected Shell shell;
	private Text text;
	private AuthenticationManager auth;
	private String password;

	public LoginPrompt() {
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public String open() {
		this.auth = AuthManagers.INSTANCE.getAuthManager();
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
		shell.setImage(SWTResourceManager.getImage(LoginPrompt.class, "/logo.ico"));
		shell.setSize(522, 154);
		shell.setText("Welcome, User");
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
				boolean correct = auth.verify(toTest);
				if (correct) {
					password = text.getText();
					shell.dispose();
				} else {
					lblNewLabel_1.setVisible(true);
				}
			}

		});

		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.setText("Quit");
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				password = null;
				shell.dispose();
			}

		});
		shell.setDefaultButton(btnNewButton);
	}

}
