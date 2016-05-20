package net.viperfish.journal.swtGui;

import java.text.DateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.wb.swt.SWTResourceManager;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.swtGui.richTextEditor.RichTextEditor;

public class JournalEditor {

	protected Object result;
	protected Shell shell;
	private Text titleText;
	private String initialTitle;
	private String initialContent;
	private RichTextEditor editor;
	private Button saveButton;
	private Journal target;
	private boolean savePressed;
	private Timer dateUpdater;
	private Label titleLabel;
	private ResourceBundle messages;

	public JournalEditor() {
		savePressed = false;
		dateUpdater = new Timer("dateUpdater");
		messages = I18NUtils.INSTANCE.getBundle();
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
		target.setDate(new Date());
	}

	private boolean contentModified() {
		return !(this.titleText.getText().equals(initialTitle) && this.editor.getText().equals(initialContent));
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(JournalEditor.class, "/logo.ico"));
		shell.setSize(1000, 800);
		shell.setText("Journal Editor");
		shell.setLayout(new GridLayout(2, false));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (contentModified()) {
					if (savePressed) {
						createTarget();
					} else {
						boolean confirm = MessageDialog.openConfirm(shell, messages.getString("action.save"),
								messages.getString("save.warning"));
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
		titleLabel.setText(messages.getString("label.title"));

		titleText = new Text(shell, SWT.BORDER);
		titleText.setMessage(messages.getString("label.titlePlaceholder"));
		GridData gd_titleText = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gd_titleText.widthHint = 670;
		gd_titleText.horizontalAlignment = GridData.FILL;
		gd_titleText.grabExcessHorizontalSpace = true;
		titleText.setLayoutData(gd_titleText);
		titleText.pack();

		editor = new RichTextEditor(shell, SWT.NONE);
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
		saveButton.setText("Save");

		this.initialTitle = target.getSubject();
		this.initialContent = target.getContent();

	}

	private boolean checkSize() {
		if (titleText.getText().length() > 200) {
			MessageDialog.openError(shell, "Exceeded max size", "Title cannot exeed 200 characters");
			return false;
		}
		if (editor.getText().length() > 5240856) {
			MessageDialog.openError(shell, "Exceeded max size", "HTML content exceeding 5 Megabytes");
			return false;
		}
		return true;
	}

}
