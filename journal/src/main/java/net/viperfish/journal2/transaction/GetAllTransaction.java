package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

final class GetAllTransaction extends TransactionWithResult<Collection<Journal>> {

	private CrudRepository<Journal, ?> db;

	GetAllTransaction(CrudRepository<Journal, ?> db) {
		super();
		this.db = db;
	}

	@Override
	public void execute() throws CipherException, CompromisedDataException, IOException {
		LinkedList<Journal> result = new LinkedList<>();
		for (Journal j : db.findAll()) {
			result.add(j);
		}
		this.setResult(result);
	}

}
