package net.viperfish.journal.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.framework.Operation;

public class SingleThreadedOperationExecutor implements OperationExecutor {

	private Thread workerThread;
	private List<Operation> queue;
	private List<Throwable> exceptions;
	private Object mutex;

	public SingleThreadedOperationExecutor() {
		queue = new LinkedList<Operation>();
		exceptions = new LinkedList<Throwable>();
		exceptions = Collections.synchronizedList(exceptions);
		mutex = new Object();
		workerThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (Thread.interrupted()) {
						return;
					}
					synchronized (mutex) {
						try {
							mutex.wait();
						} catch (InterruptedException e) {
							return;
						}
						if (!queue.isEmpty()) {
							for (Operation i : queue) {
								try {
									i.execute();
								} catch (Throwable e) {
									exceptions.add(e);
									e.printStackTrace();
								}
								queue.remove(i);
							}
						}
					}
				}

			}

		});
		workerThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.viperfish.journal.framework.OperationExecutor#submit(net.viperfish
	 * .journal.framework.Operation)
	 */
	@Override
	public void submit(Operation o) {
		synchronized (mutex) {
			queue.add(o);
			mutex.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.viperfish.journal.framework.OperationExecutor#hasException()
	 */
	@Override
	public boolean hasException() {
		return !exceptions.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.viperfish.journal.framework.OperationExecutor#getNextError()
	 */
	@Override
	public Throwable getNextError() {
		return exceptions.remove(0);
	}

	@Override
	public void terminate() {
		this.workerThread.interrupt();
		try {
			this.workerThread.join(5000);
		} catch (InterruptedException e) {
			return;
		}
	}
}
