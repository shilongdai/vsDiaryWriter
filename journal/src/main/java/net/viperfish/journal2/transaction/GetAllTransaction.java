package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.TransactionWithResult;

final class GetAllTransaction extends TransactionWithResult<Collection<Journal>> {

	private JournalEncryptor enc;
	private CrudRepository<Journal, ?> db;

	GetAllTransaction(JournalEncryptor enc, CrudRepository<Journal, ?> db) {
		super();
		this.enc = enc;
		this.db = db;
	}

	@Override
	public void execute() {
		List<Journal> result = new LinkedList<>();
		for (Journal j : db.findAll()) {
			result.add(enc.decryptJournal(j));
		}
		this.setResult(result);
	}

}
