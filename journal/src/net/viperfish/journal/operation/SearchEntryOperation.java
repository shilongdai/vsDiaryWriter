package net.viperfish.journal.operation;

import java.util.HashSet;
import java.util.Set;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.utils.index.Indexer;

public class SearchEntryOperation implements OperationWithResult<Set<Journal>> {

	private String query;
	private Set<Journal> result;
	private boolean done;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private static boolean firstTime;

	static {
		firstTime = true;
	}

	public SearchEntryOperation(String query) {
		this.query = query;
		result = new HashSet<Journal>();
		done = false;
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

	@Override
	public void execute() {
		try {
			if (firstTime) {
				if (indexer.isMemoryBased()) {
					for (Journal j : db.getAll()) {
						indexer.add(j);
					}
				}
				firstTime = false;
			}
			Iterable<Long> indexResult = indexer.search(query);
			for (Long id : indexResult) {
				Journal j = db.getEntry(id);
				if (j == null) {
					indexer.delete(id);
					continue;
				}
				result.add(j);
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
