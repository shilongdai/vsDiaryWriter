package net.viperfish.journal2.core;

public abstract class TransactionWithResult<T> implements Transaction {

	private boolean isDone;
	private T result;

	protected void setResult(T result) {
		this.result = result;
		isDone = true;
	}

	/**
	 * check whether this has finished executing
	 * 
	 * @return is complete
	 */
	public boolean isDone() {
		return isDone;
	}

	/**
	 * get the result
	 * 
	 * This method gets the result of the operation. It blocks until the result
	 * is available. It will block for no more 1 minute. If exceeds one minute,
	 * it is interrupted and a {@link RuntimeException} is thrown.
	 * 
	 * @return the result of the operation
	 */
	public T getResult() {
		return result;
	}

}
