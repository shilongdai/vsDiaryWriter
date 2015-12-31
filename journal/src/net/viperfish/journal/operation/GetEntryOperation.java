package net.viperfish.journal.operation;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;

public class GetEntryOperation implements OperationWithResult<Journal> {

	private Long id;
	private boolean done;
	private Journal result;
	private EntryDatabase db;
	private JournalTransformer t;

	public GetEntryOperation(Long id) {
		this.id = id;
		done = false;
		db = ComponentProvider.getEntryDatabase();
		result = new Journal();
		t = ComponentProvider.getTransformer();
		t.setPassword(JournalApplication.getPassword());
	}

	@Override
	public void execute() {
		try {
			result = t.decryptJournal(db.getEntry(id));
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
