package net.viperfish.journal.swtGui.conf;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SetupChooserDialog extends Dialog {

	private Group radioGroup;
	private Button expressConfiguration;
	private Button advancedButton;
	private boolean isAdvance;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SetupChooserDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Label description = new Label(container, SWT.NONE);
		description.setText("Select a configuration option:");

		isAdvance = false;

		radioGroup = new Group(container, SWT.CENTER);
		radioGroup.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		radioGroup.setLayout(new RowLayout(SWT.VERTICAL));

		expressConfiguration = new Button(radioGroup, SWT.RADIO);
		expressConfiguration.setSelection(true);
		expressConfiguration.setText("Express Configuration (recommended for most users)");
		expressConfiguration.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				super.widgetSelected(arg0);
				isAdvance = false;
			}

		});
		advancedButton = new Button(radioGroup, SWT.RADIO);
		advancedButton.setText("Advanced Configuration (recommended for expert users)");
		advancedButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				super.widgetSelected(arg0);
				isAdvance = true;
			}

		});

		return container;
	}

	public boolean isAdvanced() {
		return isAdvance;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Next", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

}
