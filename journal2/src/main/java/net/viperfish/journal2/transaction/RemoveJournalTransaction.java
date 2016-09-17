package net.viperfish.journal2.transaction;

import org.springframework.data.repository.CrudRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;

final class RemoveJournalTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> db;
	private JournalIndexer indexer;
	private JournalEncryptor enc;
	private Long id;

	public RemoveJournalTransaction(Long id, CrudRepository<Journal, Long> db, JournalIndexer indexer,
			JournalEncryptor enc) {
		this.db = db;
		this.indexer = indexer;
		this.id = id;
		this.enc = enc;
	}

	@Override
	public void execute() {
		Journal toRemove = db.findOne(id);
		toRemove = enc.decryptJournal(toRemove);

		db.delete(id);
		indexer.delete(id);

		this.setResult(toRemove);
	}

}
