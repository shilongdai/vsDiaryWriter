package net.viperfish.journal.gui;

import java.awt.Font;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.viperfish.journal.framework.UserInterface;
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
		/**
		 * String password = new String();
		while (true) {
			out.print("password:");
			out.flush();
			password = new String(display.readPassword());
			if (!isPasswordSet()) {
				setPassword(password);
				break;
			}
			if (authenticate(password)) {
				break;
			}
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				return new String();
			}
			out.println("incorrect password, please retry");
			out.flush();
		}
		return password;
		 */
		return "";
	}

}
