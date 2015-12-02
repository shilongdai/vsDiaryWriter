package net.viperfish.journal.operation;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;

public class GetEntryOperation implements OperationWithResult<Journal> {

	private Long id;
	private boolean done;
	private Journal result;
	private EntryDatabase db;

	public GetEntryOperation(Long id) {
		this.id = id;
		done = false;
		db = JournalApplication.getDataSourceFactory().createDatabaseObject();
		result = new Journal();
	}

	@Override
	public void execute() {
		try {
			result = db.getEntry(id);
		} finally {
			synchronized (this) {
				done = true;
				this.notifyAll();
			}
		}
	}

	@Override
	public synchronized boolean isDone() {
		// TODO Auto-generated method stub
		return done;
	}

	@Override
	public Journal getResult() {
		if (!done) {
			synchronized (this) {
				while (true) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					if (done) {
						break;
					}
				}

			}
		}
		return result;
	}

}
