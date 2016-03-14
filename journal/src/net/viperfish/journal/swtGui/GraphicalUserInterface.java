package net.viperfish.journal.swtGui;

import org.eclipse.jface.dialogs.IDialogConstants;

import net.viperfish.journal.framework.ConfigPage;
import net.viperfish.journal.framework.ConfigPages;
import net.viperfish.journal.swtGui.conf.ConfigurationOption;
import net.viperfish.journal.swtGui.conf.JournalSetup;
import net.viperfish.journal.swtGui.conf.SetupChooserDialog;
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
		SetupChooserDialog choose = new SetupChooserDialog(null);
		int result = choose.open();
		if (result == IDialogConstants.CANCEL_ID) {
			throw new TerminationControlFlowException();
		}
		boolean isAdvanced = choose.isAdvanced();
		if (isAdvanced) {
			if (!setup.open(ConfigurationOption.SETUP)) {
				throw new TerminationControlFlowException();
			}
		} else {
			for (Class<? extends ConfigPage> i : ConfigPages.getConfigs()) {
				try {
					ConfigPage p = i.newInstance();
					p.saveDefault();
				} catch (InstantiationException | IllegalAccessException e) {
					System.err.println("Failed to load preference page " + i + ":" + e.getMessage());
					e.printStackTrace();
				}
			}
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
