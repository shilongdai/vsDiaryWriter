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
		shell.setSize(800, 1200);
		shell.setText("Journal Editor");
		shell.setLayout(new GridLayout(1, false));
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
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		lblNewLabel.setText("Title:");

		text = new Text(shell, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gd_text.widthHint = 670;
		gd_text.horizontalAlignment = GridData.FILL;
		gd_text.grabExcessHorizontalSpace = true;
		text.setLayoutData(gd_text);
		text.pack();

		editor = new RichTextEditor(shell, SWT.NONE);
		GridData gd_browser = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		gd_browser.widthHint = 690;
		gd_browser.heightHint = 450;
		gd_browser.grabExcessHorizontalSpace = true;
		gd_browser.grabExcessVerticalSpace = true;
		gd_browser.horizontalAlignment = GridData.FILL;
		gd_browser.verticalAlignment = GridData.FILL;
		editor.setLayoutData(gd_browser);
		editor.pack();

		text.setText(target.getSubject());
		editor.setText(target.getContent());

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
	}

}
