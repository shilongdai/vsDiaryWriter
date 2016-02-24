package net.viperfish.journal.ui;

import net.viperfish.journal.framework.Observable;
import net.viperfish.journal.framework.Operation;

/**
 * An operation executor that will call the execute method on an operation
 * 
 * @author sdai
 *
 */
public abstract class OperationExecutor extends Observable<Throwable> {

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