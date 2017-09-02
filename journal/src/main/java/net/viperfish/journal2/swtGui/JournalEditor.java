package net.viperfish.journal2.swtGui;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalI18NBundle;

public class JournalEditor {

	protected Object result;
	protected Shell shell;
	private Text titleText;
	private String initialTitle;
	private String initialContent;
	private StyledText editor;
	private Button saveButton;
	private Journal target;
	private boolean savePressed;
	private Timer dateUpdater;
	private Label titleLabel;

	public JournalEditor() {
		savePressed = false;
		dateUpdater = new Timer("dateUpdater");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 * @wbp.parser.entryPoint
	 */
	public Journal open(Journal j) {
		this.target = j;
		createContents();
		shell.open();
		shell.layout();
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		dateUpdater.cancel();
		return target;
	}

	private void createTarget() {
		target.setId(target.getId());
		target.setSubject(titleText.getText());
		target.setContent(editor.getText());
		target.setTimestamp(new Date());
	}

	private boolean contentModified() {
		return !(this.titleText.getText().equals(initialTitle) && this.editor.getText().equals(initialContent));
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell();
		shell.setSize(1000, 800);
		shell.setText(JournalI18NBundle.getString("journal2.journalEditor"));
		shell.setLayout(new GridLayout(2, false));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (contentModified()) {
					if (savePressed) {
						createTarget();
					} else {
						boolean confirm = MessageDialog.openConfirm(shell,
								JournalI18NBundle.getString("journal2.journalEditor.saveBeforeExit.title"),
								JournalI18NBundle.getString("journal2.journalEditor.saveBeforeExit"));
						if (confirm) {
							if (checkSize()) {
								createTarget();
							}
						} else {
							target = null;
							shell.dispose();
						}
					}
				} else {
					target = null;
				}
			}
		});

		final Label dateDisplayer = new Label(shell, SWT.NONE);
		dateDisplayer.setText("Date");
		GridData gd_dateDisplayer = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd_dateDisplayer.horizontalSpan = 2;
		dateDisplayer.setLayoutData(gd_dateDisplayer);
		dateUpdater.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
				Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (dateDisplayer.isDisposed()) {
							return;
						}
						dateDisplayer.setText(df.format(new Date()));
						dateDisplayer.pack();
					}
				});

			}
		}, 0, 1001);

		titleLabel = new Label(shell, SWT.NONE);
		titleLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		titleLabel.setText(JournalI18NBundle.getString("journal.title"));

		titleText = new Text(shell, SWT.BORDER);
		titleText.setMessage(JournalI18NBundle.getString("journal.title.placeholder"));
		GridData gd_titleText = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gd_titleText.widthHint = 670;
		gd_titleText.horizontalAlignment = GridData.FILL;
		gd_titleText.grabExcessHorizontalSpace = true;
		titleText.setLayoutData(gd_titleText);
		titleText.pack();

		editor = new StyledText(shell, SWT.NONE);
		GridData gd_browser = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_browser.horizontalSpan = 2;
		gd_browser.widthHint = 690;
		gd_browser.heightHint = 450;
		gd_browser.grabExcessHorizontalSpace = true;
		gd_browser.grabExcessVerticalSpace = true;
		gd_browser.horizontalAlignment = GridData.FILL;
		gd_browser.verticalAlignment = GridData.FILL;
		editor.setLayoutData(gd_browser);
		editor.pack();
		editor.setText(target.getContent());

		titleText.setText(target.getSubject());
		new Label(shell, SWT.NONE);

		saveButton = new Button(shell, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 64;
		saveButton.setLayoutData(gd_btnNewButton);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (checkSize()) {
					savePressed = true;
					shell.dispose();
				}
			}
		});
		saveButton.setText(JournalI18NBundle.getString("label.save"));

		this.initialTitle = target.getSubject();
		this.initialContent = target.getContent();

	}

	private boolean checkSize() {
		if (titleText.getText().length() > 200) {
			MessageDialog.openError(shell, JournalI18NBundle.getString("journal2.journalEditor.size.title"),
					JournalI18NBundle.getString("journal2.journalEditor.size.subject"));
			return false;
		}
		if (editor.getText().length() > 5240856) {
			MessageDialog.openError(shell, JournalI18NBundle.getString("journal2.journalEditor.size.title"),
					JournalI18NBundle.getString("journal2.journalEditor.size.content"));
			return false;
		}
		return true;
	}

}
