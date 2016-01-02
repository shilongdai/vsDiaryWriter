package net.viperfish.journal.operation;

import java.util.HashSet;
import java.util.Set;

import net.viperfish.journal.ComponentProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.OperationWithResult;
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
		db = ComponentProvider.getEntryDatabase(Configuration.getString(ConfigMapping.DB_COMPONENT));
		indexer = ComponentProvider.getIndexer(Configuration.getString(ConfigMapping.INDEXER_COMPONENT));
		t = ComponentProvider.getTransformer(Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT));

	}

	@Override
	public void execute() {
		try {
			t.setPassword(ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT))
					.getPassword());
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
