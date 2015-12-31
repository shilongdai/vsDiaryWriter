package net.viperfish.journal.operation;

import java.util.HashSet;
import java.util.Set;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.index.Indexer;

public class SearchEntryOperation implements OperationWithResult<Set<Journal>> {

	private String query;
	private Set<Journal> result;
	private boolean done;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private JournalTransformer t;

	public SearchEntryOperation(String query) {
		this.query = query;
		result = new HashSet<Journal>();
		done = false;
		db = ComponentProvider.getEntryDatabase(JournalApplication.getSysConf().getProperty("DataStorage"));
		indexer = ComponentProvider.getIndexer(JournalApplication.getSysConf().getProperty("Indexer"));
		t = ComponentProvider.getTransformer();
		t.setPassword(JournalApplication.getPassword());
	}

	@Override
	public void execute() {
		try {
			Iterable<Long> indexResult = indexer.search(query);
			for (Long id : indexResult) {
				Journal j = db.getEntry(id);
				if (j == null) {
					indexer.delete(id);
					continue;
				}
				result.add(t.decryptJournal(j));
			}
		} finally {
			done = true;
			synchronized (this) {
				this.notifyAll();
			}
		}
	}

	@Override
	public synchronized boolean isDone() {
		return done;
	}

	@Override
	public synchronized Set<Journal> getResult() {
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
				break;
			}
		}
		return result;
	}

}
