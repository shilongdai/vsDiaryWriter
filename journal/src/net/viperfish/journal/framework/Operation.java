package net.viperfish.journal.framework;

/**
 * A operation to be completed, based on the command pattern
 * 
 * @author sdai
 *
 */
public interface Operation {
	/**
	 * the task to run
	 */
	public void execute();
}
