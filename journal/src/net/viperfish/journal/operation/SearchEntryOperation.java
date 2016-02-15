package net.viperfish.journal.operation;

import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.utils.index.Indexer;

/**
 * search the system for entry matching keywords
 * 
 * @author sdai
 *
 */
public class SearchEntryOperation extends OperationWithResult<Set<Journal>> {

	private String query;
	private EntryDatabase db;
	private Indexer<Journal> indexer;
	private static boolean firstTime;

	static {
		firstTime = true;
	}

	public SearchEntryOperation(String query) {
		this.query = query;
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();

	}

	@Override
	public void execute() {
		Set<Journal> searched = new TreeSet<>();
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
				searched.add(j);
			}
		} finally {
			setResult(searched);
		}
	}

}
