package net.viperfish.journal.swtGui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ErrorDialog extends Dialog {

	protected Object result;
	protected Shell shlError;
	private Text exceptionType;
	private Throwable e;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public ErrorDialog(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open(Throwable error) {
		this.e = error;
		createContents();
		shlError.open();
		shlError.layout();
		Display display = getParent().getDisplay();
		while (!shlError.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlError = new Shell(getParent(), getStyle());
		shlError.setSize(450, 300);
		shlError.setText("Error");
		shlError.setLayout(new GridLayout(2, false));

		Label exceptionLabel = new Label(shlError, SWT.NONE);
		exceptionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		exceptionLabel.setText("Exception:");

		exceptionType = new Text(shlError, SWT.BORDER);
		exceptionType.setEditable(false);
		exceptionType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		StyledText stackTrace = new StyledText(shlError, SWT.BORDER);
		stackTrace.setDoubleClickEnabled(false);
		stackTrace.setEditable(false);
		stackTrace.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		exceptionType.setText(e.getMessage());
		StackTraceElement[] elements = e.getStackTrace();
		for (StackTraceElement i : elements) {
			stackTrace.append(i.toString());
			stackTrace.append("\n");
		}
	}

}
