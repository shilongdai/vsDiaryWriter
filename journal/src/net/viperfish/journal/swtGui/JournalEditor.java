package net.viperfish.journal.swtGui;

import java.util.Date;

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

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.swtGui.richTextEditor.RichTextEditor;

public class JournalEditor {

	protected Object result;
	protected Shell shell;
	private Text text;
	private RichTextEditor editor;
	private Button saveButton;
	private Journal target;
	private Button btnNewButton;
	private boolean savePressed;

	public JournalEditor() {
		savePressed = false;
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
		return target;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell();
		shell.setSize(690, 454);
		shell.setText("Journal Editor");
		shell.setLayout(new GridLayout(3, false));
		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (savePressed) {
					return;
				} else {
					target = null;
				}

			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.widthHint = 31;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		lblNewLabel.setText("Title:");

		text = new Text(shell, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_text.widthHint = 437;
		text.setLayoutData(gd_text);

		editor = new RichTextEditor(shell, SWT.NONE);
		GridData gd_browser = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 11);
		gd_browser.heightHint = 324;
		gd_browser.widthHint = 678;
		editor.setLayoutData(gd_browser);

		text.setText(target.getSubject());
		editor.setText(target.getContent());
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		saveButton = new Button(shell, SWT.NONE);
		GridData gd_btnNewButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNewButton.widthHint = 64;
		saveButton.setLayoutData(gd_btnNewButton);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				savePressed = true;
				target.setSubject(text.getText());
				target.setContent(editor.getText());
				target.setDate(new Date());
				shell.dispose();
			}
		});
		saveButton.setText("Save");

		btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.setText("Cancel");
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				target = null;
				shell.dispose();
			}

		});
		new Label(shell, SWT.NONE);
	}

}
