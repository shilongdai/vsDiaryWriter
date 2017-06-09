package net.viperfish.journal2.transaction;

import org.springframework.data.repository.CrudRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.TextIndexFieldEncryptor;

final class UpdateJournalTransaction extends TransactionWithResult<Journal> {

    private CrudRepository<Journal, Long> db;
    private JournalIndexer indexer;
    private JournalEncryptor enc;
    private Journal toUpdate;
    private TextIndexFieldEncryptor indexCrypt;

    UpdateJournalTransaction(CrudRepository<Journal, Long> db, JournalIndexer indexer, JournalEncryptor enc, TextIndexFieldEncryptor indexCrypt,
            Journal toUpdate) {
        super();
        this.db = db;
        this.indexer = indexer;
        this.enc = enc;
        this.toUpdate = toUpdate;
        this.indexCrypt = indexCrypt;
    }

    @Override
    public void execute() {
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
