package net.viperfish.journal.swtGui.richTextEditor;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class RichTextEditor extends Composite {

	private Browser browser;
	private String editor_content;
	private boolean loadCompleted = false;

	private static boolean useMozilla = false;

	static {
		File xulRunner = new File("./xulrunner");
		if (xulRunner.exists()) {
			System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulRunner.getAbsolutePath());
			useMozilla = true;
		}
	}

	public RichTextEditor(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, true));
		if (useMozilla) {
			browser = new Browser(this, SWT.MOZILLA);
		} else {
			browser = new Browser(this, SWT.NONE);
		}
		browser.setJavascriptEnabled(true);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setDragDetect(true);

		browser.setUrl(new File("editor/index.html").toURI().toString());

		// Set content of editor after load completed
		browser.addProgressListener(new ProgressListener() {
			@Override
			public void changed(ProgressEvent event) {
			}

			@Override
			public void completed(ProgressEvent event) {
				if (!loadCompleted) {
					loadCompleted = true;
					setText(editor_content);
				}
			}

		});

		// Listen to status change event
		browser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent event) {
				browser.setData("content", event.text);
			}
		});
	}

	/**
	 * Set the content of the HTML editor.
	 * 
	 * @param String
	 *            text
	 */
	public void setText(String text) {
		editor_content = text == null ? "" : text.replace("\n", "");

		if (loadCompleted) {
			/**
			 * [TimPietrusky] 20120416 - tinyMCE might not yet been "completely"
			 * initialized
			 */
			browser.execute("setContent('" + editor_content + "');");
		}
	}

	/**
	 * Returns the content of the HTML editor.
	 * 
	 * @return String
	 */
	public String getText() {
		String content = "";

		boolean executed = browser.execute("window.status=getContent();");

		if (executed) {
			content = (String) browser.getData("content");
			this.editor_content = content;
		}

		return content;
	}

}