package net.viperfish.journal.framework;

/**
 * a Operation with a result
 * 
 * @author sdai
 *
 * @param <T>
 *            the result type
 */
public interface OperationWithResult<T> extends Operation {
	/**
	 * check whether this has finished executing
	 * 
	 * @return if complete
	 */
	public boolean isDone();

	/**
	 * get the result, will block if the operation is not executed
	 * 
	 * @return the result of the operation
	 */
	public T getResult();
}
