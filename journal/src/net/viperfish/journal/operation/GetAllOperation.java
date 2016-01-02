package net.viperfish.journal.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;

public class GetAllOperation implements OperationWithResult<List<Journal>> {

	private EntryDatabase db;
	private List<Journal> result;
	private boolean done;
	private JournalTransformer t;

	public GetAllOperation() {
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		t = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));
		result = new LinkedList<>();
	}

	@Override
	public void execute() {
		try {
			t.setPassword(ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT))
					.getPassword());
			List<Journal> tmp = db.getAll();
			for (Journal j : tmp) {
				result.add(t.decryptJournal(j));
			}
			Collections.sort(result);
		} finally {
			synchronized (this) {
				this.notifyAll();
				done = true;
			}
		}
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	@Override
	public synchronized List<Journal> getResult() {
		if (done) {
			return result;
		}
		while (true) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return null;
			}
			if (done) {
				return result;
			}
		}
	}

}
