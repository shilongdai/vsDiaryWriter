package net.viperfish.journal.swtGui;

import net.viperfish.journal.swtGui.conf.JournalSetup;
import net.viperfish.journal.ui.TerminationControlFlowException;
import net.viperfish.journal.ui.UserInterface;

public class GraphicalUserInterface extends UserInterface {

	private JournalWindow w;

	private JournalSetup setup;

	private LoginPrompt prompt;

	private SetPasswordPrompt setPassword;

	public GraphicalUserInterface() {
		setPassword = new SetPasswordPrompt();
		w = new JournalWindow();
		setup = new JournalSetup();
		prompt = new LoginPrompt();
		setPassword = new SetPasswordPrompt();
	}

	@Override
	public void run() {
		w.open();
		System.err.println("windows closed");
	}

	@Override
	public void setup() throws TerminationControlFlowException {
		if (!setup.open()) {
			throw new TerminationControlFlowException();
		}
	}

	@Override
	public String promptPassword() throws TerminationControlFlowException {
		String result = prompt.open();
		if (result == null) {
			throw new TerminationControlFlowException();
		} else {
			return result;
		}
	}

	@Override
	public void setFirstPassword() throws TerminationControlFlowException {
		boolean passwordSet = setPassword.open(PasswordOperation.SET);
		if (!passwordSet) {
			throw new TerminationControlFlowException();
		}
	}

}
