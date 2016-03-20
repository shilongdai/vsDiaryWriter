package net.viperfish.journal.framework.operationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationExecutor;

public class ThreadPoolOperationExecutor extends OperationExecutor {

	private ExecutorService pool;

	private class OperationRunner implements Runnable {

		private Operation o;

		public OperationRunner(Operation toRun) {
			this.o = toRun;
		}

		@Override
		public void run() {
			try {
				o.execute();
			} catch (Throwable s) {
				ThreadPoolOperationExecutor.this.notifyObservers(s);
			}

		}

	}

	public ThreadPoolOperationExecutor() {
		pool = Executors.newSingleThreadExecutor();
	}

	@Override
	public void submit(Operation o) {
		pool.submit(new OperationRunner(o));
	}

	@Override
	public void terminate() {
		pool.shutdown();

	}

}
