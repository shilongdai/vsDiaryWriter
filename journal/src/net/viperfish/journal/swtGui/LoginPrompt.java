package net.viperfish.journal.swtGui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import net.viperfish.journal.framework.OperationExecutors;

public class LoginPrompt {

	protected Shell shell;
	private Text text;
	private String password;
	private Display display;
	private final ExecutorService exc = Executors.newCachedThreadPool();

	public LoginPrompt() {

	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public String open() {

		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		exc.shutdown();
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
		shell.setLayout(new GridLayout(4, false));

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Password");

		text = new Text(shell, SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		new Label(shell, SWT.NONE);

		final Label hashPasswordLabel = new Label(shell, SWT.NONE);
		hashPasswordLabel.setText("Hashing Password:");

		final ProgressBar hashBar = new ProgressBar(shell, SWT.INDETERMINATE | SWT.SMOOTH);
		hashBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 2, 1));

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setText("Enter");

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

		final Label lblNewLabel_1 = new Label(shell, SWT.NONE);
		lblNewLabel_1.setText("Password mismatch");
		lblNewLabel_1.setVisible(false);
		new Label(shell, SWT.NONE);

		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				display.asyncExec(new Runnable() {

					@Override
					public void run() {
						hashBar.setVisible(true);
						hashPasswordLabel.setVisible(true);
					}
				});
				final String toTest = text.getText();

				exc.execute(new Runnable() {

					@Override
					public void run() {
						VerifyPasswordOperation vp = new VerifyPasswordOperation(toTest);
						OperationExecutors.getExecutor().submit(vp);
						boolean correct = vp.getResult();
						if (correct) {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									password = text.getText();
									shell.dispose();
								}
							});
						} else {
							display.asyncExec(new Runnable() {

								@Override
								public void run() {
									lblNewLabel_1.setVisible(true);
									hashBar.setVisible(false);
									hashPasswordLabel.setVisible(false);
								}
							});
						}
					}
				});
			}

		});

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				lblNewLabel_1.setVisible(false);
				hashBar.setVisible(false);
				hashPasswordLabel.setVisible(false);
			}
		});

		hashBar.setVisible(false);
		hashPasswordLabel.setVisible(false);

	}

}
