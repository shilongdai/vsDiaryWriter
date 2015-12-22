package net.viperfish.journal.swingGui.setup.textEditor;

import java.util.Date;

import javax.swing.JLabel;

public class DateLabel extends JLabel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4487545071051416166L;
	private boolean running = false;
	private Thread thread;

	@Override
	public void run() {
		while (running) {
			if (parentDisposed()) {
				break;
			}
			updateDate();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		running = false;
	}

	private void updateDate() {
		Date date = new Date();
		String dateString = date.toString();
		setText(dateString);
	}

	@Override
	public String getText() {
		updateDate();
		return super.getText();
	}

	public boolean parentDisposed() {
		return !getParent().isVisible();
	}

	public void start() {
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {

	}

}
