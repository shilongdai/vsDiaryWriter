package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.JournalEncryptorChain;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

final class GetAllTransaction extends TransactionWithResult<Collection<Journal>> {

	private JournalEncryptorChain enc;
	private CrudRepository<Journal, ?> db;

	GetAllTransaction(JournalEncryptorChain enc, CrudRepository<Journal, ?> db) {
		super();
		this.enc = enc;
		this.db = db;
	}

	@Override
	public void execute() throws CipherException, CompromisedDataException, IOException {
		List<Journal> result = new LinkedList<>();
		for (Journal j : db.findAll()) {
			result.add(enc.decryptJournal(j));
		}
		this.setResult(result);
	}

}
