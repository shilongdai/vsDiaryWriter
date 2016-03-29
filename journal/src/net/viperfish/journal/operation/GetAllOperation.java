package net.viperfish.journal.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalPointer;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * gets all entries in the system
 * 
 * @author sdai
 *
 */
final class GetAllOperation extends OperationWithResult<List<JournalPointer>> {

	GetAllOperation() {
	}

	@Override
	public void execute() {
		List<Journal> all = new LinkedList<>();
		List<JournalPointer> results = new LinkedList<>();
		try {
			all = db().getAll();
			for (Journal i : all) {
				results.add(new JournalPointer(i));
			}
			Collections.sort(results);
		} finally {
			setResult(results);
		}
	}

}
