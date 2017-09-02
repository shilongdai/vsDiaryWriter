package net.viperfish.journal2.swtGui;

import org.eclipse.swt.widgets.Display;

import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.crypt.AEADPreferencePage;
import net.viperfish.journal2.crypt.CompressionPreferencePage;
import net.viperfish.journal2.crypt.HMACPreferencePage;
import net.viperfish.journal2.crypt.StreamCipherPreferencePage;

public class GraphicalUserInterface {

	private JournalWindow w;

	private LoginPrompt prompt;

	private SetPasswordPrompt setPassword;

	public GraphicalUserInterface(JournalService service, AuthenticationManager authManager) {
		Display.setAppName("VSDiaryWriter");
		w = new JournalWindow(service, new AEADPreferencePage(), new CompressionPreferencePage(),
				new HMACPreferencePage(), new StreamCipherPreferencePage());
		prompt = new LoginPrompt(authManager);
		setPassword = new SetPasswordPrompt(authManager);
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
