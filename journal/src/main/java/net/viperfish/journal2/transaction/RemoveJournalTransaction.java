package net.viperfish.journal2.transaction;

import java.io.IOException;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.JournalEncryptorChain;

final class RemoveJournalTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> db;
	private JournalIndexer indexer;
	private JournalEncryptorChain enc;
	private Long id;

	public RemoveJournalTransaction(Long id, CrudRepository<Journal, Long> db, JournalIndexer indexer,
			JournalEncryptorChain enc) {
		this.db = db;
		this.indexer = indexer;
		this.id = id;
		this.enc = enc;
	}

	@Override
	public void execute() throws IOException {
		Journal toRemove = db.findOne(id);
		toRemove = enc.decryptJournal(toRemove);

		db.delete(id);
		indexer.delete(id);

		this.setResult(toRemove);
	}

}
