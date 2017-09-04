package net.viperfish.journal2.transaction;

import java.io.IOException;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.JournalEncryptorChain;
import net.viperfish.journal2.crypt.TextIndexFieldEncryptor;

final class UpdateJournalTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> db;
	private JournalIndexer indexer;
	private JournalEncryptorChain enc;
	private Journal toUpdate;
	private TextIndexFieldEncryptor indexCrypt;

	UpdateJournalTransaction(CrudRepository<Journal, Long> db, JournalIndexer indexer, JournalEncryptorChain enc,
			TextIndexFieldEncryptor indexCrypt, Journal toUpdate) {
		super();
		this.db = db;
		this.indexer = indexer;
		this.enc = enc;
		this.toUpdate = toUpdate;
		this.indexCrypt = indexCrypt;
	}

	@Override
	public void execute() throws IOException {
		indexer.delete(toUpdate.getId());
		Journal toIndex = new Journal(toUpdate);
		toIndex.setId(toUpdate.getId());
		toIndex.setSubject(indexCrypt.cryptStringWords(toUpdate.getSubject()));
		indexer.add(toIndex);

		toUpdate = enc.encryptJournal(toUpdate);
		db.save(toUpdate);

		this.setResult(enc.decryptJournal(toUpdate));
	}

}
