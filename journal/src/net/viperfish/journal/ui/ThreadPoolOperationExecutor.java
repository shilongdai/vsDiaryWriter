package net.viperfish.journal.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationExecutor;

public class ThreadPoolOperationExecutor implements OperationExecutor {

	private ExecutorService pool;
	private List<Throwable> errors;

	private class ExceptionHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			errors.add(e);

		}

	}

	public ThreadPoolOperationExecutor() {
		errors = new LinkedList<Throwable>();
		errors = java.util.Collections.synchronizedList(errors);
		pool = Executors.newCachedThreadPool(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setUncaughtExceptionHandler(new ExceptionHandler());
				return t;
			}
		});
	}

	@Override
	public void submit(Operation o) {
		pool.submit(new Runnable() {

			@Override
			public void run() {
				o.execute();

			}
		});

	}

	@Override
	public boolean hasException() {
		return !errors.isEmpty();
	}

	@Override
	public Throwable getNextError() {
		return errors.get(0);
	}

	@Override
	public void terminate() {
		pool.shutdown();

	}

}
