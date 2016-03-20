package net.viperfish.journal.operation;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.framework.provider.EntryDatabases;

/**
 * gets an entry from the system
 * 
 * @author sdai
 *
 */
public class GetEntryOperation extends OperationWithResult<Journal> {

	private Long id;
	private EntryDatabase db;

	public GetEntryOperation(Long id) {
		this.id = id;
		db = EntryDatabases.INSTANCE.getEntryDatabase();

	}

	@Override
	public void execute() {
		Journal e = null;
		try {
			e = db.getEntry(id);
		} finally {
			setResult(e);
		}
	}

}
