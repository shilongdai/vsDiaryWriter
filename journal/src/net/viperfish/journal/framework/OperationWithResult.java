package net.viperfish.journal.framework;

/**
 * a Operation with a result
 * 
 * This class is thread safe
 * 
 * @author sdai
 *
 * @param <T>
 *            the result type
 */
public abstract class OperationWithResult<T> extends InjectedOperation {

	private boolean isDone;
	private T result;

	protected synchronized void setResult(T result) {
		this.result = result;
		this.notifyAll();
		isDone = true;
	}

	/**
	 * check whether this has finished executing
	 * 
	 * @return is complete
	 */
	public synchronized boolean isDone() {
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
		if (!isDone) {
			synchronized (this) {
				while (true) {
					try {
						this.wait(60000);
					} catch (InterruptedException e) {
						throw new RuntimeException("Operation Time Out");
					}
					if (isDone) {
						break;
					}
				}

			}
		}
		return result;
	}
}
