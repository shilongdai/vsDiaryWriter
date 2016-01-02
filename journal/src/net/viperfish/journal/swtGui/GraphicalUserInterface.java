package net.viperfish.journal.swtGui;

import net.viperfish.journal.ConfigMapping;
import net.viperfish.journal.provider.ComponentProvider;
import net.viperfish.journal.swtGui.conf.JournalSetup;
import net.viperfish.journal.ui.UserInterface;

public class GraphicalUserInterface extends UserInterface {

	private JournalWindow w;

	private JournalSetup setup;

	private LoginPrompt prompt;

	public GraphicalUserInterface() {
	}

	@Override
	public void run() {
		w = new JournalWindow();
		w.open();
		System.err.println("windows closed");
	}

	@Override
	public void setup() {
		setup = new JournalSetup();
		setup.open();
	}

	@Override
	public String promptPassword() {
		prompt = new LoginPrompt(ComponentProvider.getAuthManager(ConfigMapping.AUTH_COMPONENT));
		return prompt.open();
	}

}
