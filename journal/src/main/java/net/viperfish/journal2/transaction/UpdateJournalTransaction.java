package net.viperfish.journal2.transaction;

import org.springframework.data.repository.CrudRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;

final class UpdateJournalTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> db;
	private JournalIndexer indexer;
	private JournalEncryptor enc;
	private Journal toUpdate;

	UpdateJournalTransaction(CrudRepository<Journal, Long> db, JournalIndexer indexer, JournalEncryptor enc,
			Journal toUpdate) {
		super();
		this.db = db;
		this.indexer = indexer;
		this.enc = enc;
		this.toUpdate = toUpdate;
	}

	@Override
	public void execute() {
		indexer.delete(toUpdate.getId());
		indexer.add(toUpdate);

		toUpdate = enc.encryptJournal(toUpdate);
		db.save(toUpdate);

		this.setResult(enc.decryptJournal(toUpdate));
	}

}
