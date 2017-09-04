package net.viperfish.journal2.transaction;

import java.io.IOException;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.TransactionWithResult;

final class SaveJournalTransaction extends TransactionWithResult<Journal> {

	private Journal toAdd;
	private CrudRepository<Journal, ?> db;

	public SaveJournalTransaction(Journal toAdd, CrudRepository<Journal, ?> db) {
		this.toAdd = toAdd;
		this.db = db;
	}

	@Override
	public void execute() throws IOException {
		Journal added = db.save(toAdd);
		this.setResult(added);
	}

}
