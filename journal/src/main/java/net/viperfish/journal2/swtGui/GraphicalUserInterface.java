package net.viperfish.journal2.swtGui;

import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;

public class GraphicalUserInterface {

	@Autowired
	private JournalWindow w;

	@Autowired
	private LoginPrompt prompt;

	@Autowired
	private SetPasswordPrompt setPassword;

	public GraphicalUserInterface() {
		Display.setAppName("VSDiaryWriter");
		Display.setAppVersion("6.0.0");
	}

	public void run() {
		w.open();
		Display.getCurrent().close();
	}

	public void promptPassword() {
		String result = prompt.open();
		if (result == null) {
			System.exit(1);
		} else {
			return;
		}
	}

	public void setFirstPassword() {
		boolean passwordSet = setPassword.open();
		if (!passwordSet) {
			System.exit(1);
		}
		return;
	}

}
