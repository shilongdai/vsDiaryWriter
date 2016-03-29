package net.viperfish.journal.operation;

import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalPointer;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * search the system for entry matching keywords
 * 
 * @author sdai
 *
 */
final class SearchEntryOperation extends OperationWithResult<Set<JournalPointer>> {

	private String query;
	private static boolean firstTime;

	static {
		firstTime = true;
	}

	SearchEntryOperation(String query) {
		this.query = query;

	}

	@Override
	public void execute() {
		Set<JournalPointer> searched = new TreeSet<>();
		try {
			if (firstTime) {
				if (indexer().isMemoryBased()) {
					for (Journal j : db().getAll()) {
						indexer().add(j);
					}
				}
				firstTime = false;
			}
			Iterable<Long> indexResult = indexer().search(query);
			for (Long id : indexResult) {
				Journal j = db().getEntry(id);
				if (j == null) {
					indexer().delete(id);
					continue;
				}
				searched.add(new JournalPointer(j));
			}
		} finally {
			setResult(searched);
		}
	}

}
