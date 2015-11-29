package net.viperfish.journal.framework;

public interface OperationWithResult<T> extends Operation {
	public boolean isDone();

	public T getResult();
}
