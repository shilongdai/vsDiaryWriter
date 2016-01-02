package net.viperfish.journal.ui;

import net.viperfish.journal.framework.Operation;

/**
 * An operation executor that will call the execute method on an operation
 * 
 * @author sdai
 *
 */
public interface OperationExecutor {

	/**
	 * submit a operation to execute later
	 * 
	 * @param o
	 *            the operation
	 */
	public abstract void submit(Operation o);

	/**
	 * check if any errors occurred
	 * 
	 * @return if error occurred
	 */
	public abstract boolean hasException();

	/**
	 * get the next caught exception
	 * 
	 * @return a caught exception
	 */
	public abstract Throwable getNextError();

	/**
	 * clean up
	 */
	public void terminate();
}