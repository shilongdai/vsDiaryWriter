package net.viperfish.journal.swtGui;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.ui.TerminationControlFlowException;
import net.viperfish.journal.ui.UserInterface;

public class GraphicalUserInterface extends UserInterface {

	private JournalWindow w;

	private LoginPrompt prompt;

	private SetPasswordPrompt setPassword;

	public GraphicalUserInterface() {
		setPassword = new SetPasswordPrompt();
		w = new JournalWindow();
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
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, "Hash");
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, "H2Database");
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, "LuceneIndexer");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, "BlockCipherMAC");
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
