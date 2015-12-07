package net.viperfish.journal.gui;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.gui.setup.CreatePassword;
import net.viperfish.journal.gui.setup.FirstTimeSetup;

public class GraphicalUserInterface extends UserInterface {

	public static Font defaultDialogTitleFont = new Font("DialogInput", Font.PLAIN, 24);
	public static Font defaultDialogOptionFont = new Font("DialogInput", Font.PLAIN, 13);

	private MainWindow window;

	public GraphicalUserInterface() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		window = new MainWindow();
		window.setVisible(true);
	}

	boolean delay = true;

	public void setup() {
		FirstTimeSetup setupMode = new FirstTimeSetup();
		new Thread(new Runnable() {
			public void run() {
				setupMode.setVisible(true);
				delay = false;
			}
		}).start();
		while (setupMode.isVisible() || delay) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String promptPassword() {
		if (!isPasswordSet()) {
			CreatePassword createPassword = new CreatePassword();
			delay = true;
			new Thread(new Runnable() {
				public void run() {
					createPassword.setVisible(true);
					delay = false;
				}
			}).start();
			while (createPassword.isVisible() || delay) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setPassword(new String(createPassword.passwordField.getPassword()));
		}
		PasswordPrompt passwordPrompt = new PasswordPrompt(this);
		delay = true;
		new Thread(new Runnable() {
			public void run() {
				passwordPrompt.setVisible(true);
				delay = false;
			}
		}).start();
		while (passwordPrompt.isVisible() || delay) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return passwordPrompt.getPassword();
	}

	public boolean auth(PasswordPrompt passwordPrompt) {
		return authenticate(passwordPrompt.getPassword());
	}

}
