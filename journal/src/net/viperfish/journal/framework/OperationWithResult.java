package net.viperfish.journal.framework;

/**
 * a Operation with a result
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
	 * @return if complete
	 */
	public synchronized boolean isDone() {
		return isDone;
	}

	/**
	 * get the result, will block if the operation is not executed
	 * 
	 * @return the result of the operation
	 */
	public T getResult() {
		if (!isDone) {
			synchronized (this) {
				while (true) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
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
