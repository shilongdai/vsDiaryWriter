package net.viperfish.journal.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationExecutor;

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
				System.err.println("starting worker");
				while (true) {
					if (Thread.interrupted()) {
						System.err.println("Thread is interrupted");
						return;
					}
					synchronized (mutex) {
						try {
							System.err.println("going to sleep");
							mutex.wait();
						} catch (InterruptedException e) {
							return;
						}
						System.out.println("Awaken");
						if (!queue.isEmpty()) {
							for (Operation i : queue) {
								try {
									System.err.println("executing:"
											+ i.getClass().getCanonicalName());
									i.execute();
								} catch (Throwable e) {
									exceptions.add(e);
									e.printStackTrace();
								}
								queue.remove(i);
								System.err.println("complete");
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
		System.err.println("notifying");
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
