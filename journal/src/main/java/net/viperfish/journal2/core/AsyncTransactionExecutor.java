package net.viperfish.journal2.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class AsyncTransactionExecutor implements TransactionExecutor {

	private ExecutorService thread;

	public AsyncTransactionExecutor() {
		thread = Executors.newSingleThreadExecutor();
	}

	@Override
	public void run(final Transaction trans) {
		thread.submit(new Runnable() {

			@Override
			public void run() {
				trans.execute();
			}
		});
	}

	@Override
	public <T> Future<T> call(final TransactionWithResult<T> trans) {
		return thread.submit(new Callable<T>() {

			@Override
			public T call() throws Exception {
				trans.execute();
				return trans.getResult();
			}
		});
	}

	@Override
	public void close() throws Exception {
		thread.shutdown();
		if (!thread.awaitTermination(5, TimeUnit.SECONDS)) {
			thread.shutdownNow();
		}
	}

}
