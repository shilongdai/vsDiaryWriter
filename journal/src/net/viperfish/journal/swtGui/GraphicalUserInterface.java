package net.viperfish.journal.swtGui;

import org.eclipse.swt.widgets.Display;

import net.viperfish.journal.ui.ExitStatus;
import net.viperfish.journal.ui.UserInterface;

public class GraphicalUserInterface extends UserInterface {

	private JournalWindow w;

	private LoginPrompt prompt;

	private SetPasswordPrompt setPassword;

	public GraphicalUserInterface() {
		Display.setAppName("VSDiaryWriter");
		Display.setAppVersion("3.1.1");
		setPassword = new SetPasswordPrompt();
		w = new JournalWindow();
		prompt = new LoginPrompt();
		setPassword = new SetPasswordPrompt();
	}

	@Override
	public void run() {
		w.open();
		Display.getCurrent().close();
	}

	@Override
	public ExitStatus promptPassword() {
		String result = prompt.open();
		if (result == null) {
			return ExitStatus.CANCEL;
		} else {
			return ExitStatus.OK;
		}
	}

	@Override
	public ExitStatus setFirstPassword() {
		boolean passwordSet = setPassword.open();
		if (!passwordSet) {
			return ExitStatus.CANCEL;
		}
		return ExitStatus.OK;
	}

}
