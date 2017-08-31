package net.viperfish.journal2.core;

import java.util.concurrent.Future;

public interface TransactionExecutor extends AutoCloseable {
	public void run(Transaction trans);

	public <T> Future<T> call(TransactionWithResult<T> trans);
}
