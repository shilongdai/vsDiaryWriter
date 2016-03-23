package net.viperfish.journal.swtGui;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MediaPlayer {

	static {
		File xulRunner = new File("./xulrunner");
		if (xulRunner.exists()) {
			System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulRunner.getAbsolutePath());
			useMozilla = true;
		}
	}

	private static boolean useMozilla = false;

	private Shell shell;
	private Browser browser;

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");
		shell.setLayout(new GridLayout(1, false));
		if (useMozilla) {
			browser = new Browser(shell, SWT.MOZILLA);
		} else {
			browser = new Browser(shell, SWT.NONE);
		}
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	public void play(String type, String base64, MediaType e) {
		String url = "data:";
		if (e == MediaType.AUDIO) {
			url += "audio/";
		} else if (e == MediaType.VIDEO) {
			url += "video/";
		}
		url += type + ";base64," + base64;
		browser.setUrl(url);
	}
}
