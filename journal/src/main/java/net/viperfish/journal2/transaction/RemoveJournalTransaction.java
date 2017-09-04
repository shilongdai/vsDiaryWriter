package net.viperfish.journal2.transaction;

import java.io.IOException;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.TransactionWithResult;

final class RemoveJournalTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> db;
	private Long id;

	public RemoveJournalTransaction(Long id, CrudRepository<Journal, Long> db) {
		this.db = db;
		this.id = id;
	}

	@Override
	public void execute() throws IOException {
		Journal toRemove = db.findOne(id);
		db.delete(id);

		this.setResult(toRemove);
	}

}
