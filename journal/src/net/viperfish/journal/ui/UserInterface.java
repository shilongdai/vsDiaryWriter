package net.viperfish.journal.ui;

/**
 * the current meta interface for UI
 * 
 * @author sdai
 *
 */
public abstract class UserInterface {

	/**
	 * start the UI loop, should block until the user selected quit
	 */
	public abstract void run();

	/**
	 * prompt password
	 * 
	 * @see UserInterface#authenticate(String)
	 * @return the valid password
	 */
	public abstract ExitStatus promptPassword();

	public abstract ExitStatus setFirstPassword();

}
