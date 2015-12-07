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
		window = new MainWindow();
		window.setVisible(true);
	}

	public void setup() {
		System.out.println("hi");
		FirstTimeSetup setupMode = new FirstTimeSetup();
		setupMode.setVisible(true);
		while (true) {
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
