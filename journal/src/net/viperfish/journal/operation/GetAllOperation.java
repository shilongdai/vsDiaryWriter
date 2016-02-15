package net.viperfish.journal.operation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * gets all entries in the system
 * 
 * @author sdai
 *
 */
public class GetAllOperation extends OperationWithResult<List<Journal>> {

	private EntryDatabase db;

	public GetAllOperation() {
		db = EntryDatabases.INSTANCE.getEntryDatabase();
	}

	@Override
	public void execute() {
		List<Journal> all = new LinkedList<>();
		try {
			all = db.getAll();
			Collections.sort(all);
		} finally {
			setResult(all);
		}
	}

}
