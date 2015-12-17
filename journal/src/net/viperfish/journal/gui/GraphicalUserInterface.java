package net.viperfish.journal.gui;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.gui.setup.CreatePassword;
import net.viperfish.journal.gui.setup.FirstTimeSetup;

public class GraphicalUserInterface extends UserInterface {

	/**
	 * Default Variables TODO Note: make sub classes like header and paragraph
	 * for jlabels
	 */
	public static Font defaultDialogTitleFont = new Font("DialogInput", Font.PLAIN, 24);
	public static Font defaultDialogOptionFont = new Font("DialogInput", Font.PLAIN, 13);

	/**
	 * Main window used to manage Journals
	 */
	private JournalWindow window;

	public GraphicalUserInterface() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		window = new JournalWindow(this);
		window.setVisible(true);
	}

	public void setup() {
		FirstTimeSetup setupMode = new FirstTimeSetup();
		openAndWaitToDispose(setupMode);
	}

	public String promptPassword() {
		if (!isPasswordSet()) {
			CreatePassword createPassword = new CreatePassword();
			openAndWaitToDispose(createPassword);
			setPassword(new String(createPassword.passwordField.getPassword()));
		}
		PasswordPrompt passwordPrompt = new PasswordPrompt(this);
		openAndWaitToDispose(passwordPrompt);
		return passwordPrompt.getPassword();
	}

	boolean delay = true;

	public void openAndWaitToDispose(final JFrame jframe) {
		delay = true;
		new Thread(new Runnable() {
			public void run() {
				jframe.setVisible(true);
				delay = false;
			}
		}).start();
		while (jframe.isVisible() || delay) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean auth(PasswordPrompt passwordPrompt) {
		return authenticate(passwordPrompt.getPassword());
	}

}
