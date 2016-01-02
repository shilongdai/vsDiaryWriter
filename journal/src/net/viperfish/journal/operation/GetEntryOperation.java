package net.viperfish.journal.operation;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;

public class GetEntryOperation implements OperationWithResult<Journal> {

	private Long id;
	private boolean done;
	private Journal result;
	private EntryDatabase db;
	private JournalTransformer t;

	public GetEntryOperation(Long id) {
		this.id = id;
		done = false;
		result = new Journal();
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		t = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));

	}

	@Override
	public void execute() {
		try {
			t.setPassword(ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT))
					.getPassword());
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
