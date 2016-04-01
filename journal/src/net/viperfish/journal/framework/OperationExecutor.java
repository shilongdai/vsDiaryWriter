package net.viperfish.journal.framework;

/**
 * An operation executor that will call the execute method on an operation
 * 
 * @author sdai
 *
 */
public abstract class OperationExecutor extends Observable<Exception> {

	/**
	 * submit a operation to execute later
	 * 
	 * @param o
	 *            the operation
	 */
	public abstract void submit(Operation o);

	/**
	 * clean up
	 */
	public abstract void terminate();
}