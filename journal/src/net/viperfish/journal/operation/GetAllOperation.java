package net.viperfish.journal.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.ConfigMapping;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;

public class GetAllOperation implements OperationWithResult<List<Journal>> {

	private EntryDatabase db;
	private List<Journal> result;
	private boolean done;
	private JournalTransformer t;

	public GetAllOperation() {
		db = ComponentProvider
				.getEntryDatabase(JournalApplication.getConfiguration().getString(ConfigMapping.DB_COMPONENT));
		t = ComponentProvider
				.getTransformer(JournalApplication.getConfiguration().getString(ConfigMapping.TRANSFORMER_COMPONENT));
		result = new LinkedList<>();
	}

	@Override
	public void execute() {
		try {
			t.setPassword(ComponentProvider
					.getAuthManager(JournalApplication.getConfiguration().getString(ConfigMapping.AUTH_COMPONENT))
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
