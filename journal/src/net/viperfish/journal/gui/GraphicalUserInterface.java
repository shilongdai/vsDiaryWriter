package net.viperfish.journal.gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.gui.setup.FirstTimeSetup;

public class GraphicalUserInterface extends UserInterface {

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
		while (true || delay) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String promptPassword() {
		return "";
	}

}
