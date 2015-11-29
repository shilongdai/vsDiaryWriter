package net.viperfish.journal.framework;

public interface OperationExecutor {

	public abstract void submit(Operation o);

	public abstract boolean hasException();

	public abstract Throwable getNextError();

	public void terminate();
}