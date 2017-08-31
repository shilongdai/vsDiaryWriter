package net.viperfish.journal2.transaction;

import org.springframework.data.repository.CrudRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.TextIndexFieldEncryptor;

final class AddJournalTransaction extends TransactionWithResult<Journal> {

    private Journal toAdd;
    private CrudRepository<Journal, ?> db;
    private JournalEncryptor enc;
    private JournalIndexer indexer;
    private TextIndexFieldEncryptor indexCrypt;

    public AddJournalTransaction(Journal toAdd, CrudRepository<Journal, ?> db, JournalIndexer indexer,
            JournalEncryptor enc, TextIndexFieldEncryptor indexCrypt) {
        this.toAdd = toAdd;
        this.db = db;
        this.enc = enc;
        this.indexer = indexer;
        this.indexCrypt = indexCrypt;
    }

    @Override
    public void execute() {
        Journal toIndex = new Journal(toAdd);
        toAdd = enc.encryptJournal(toAdd);
        toAdd = this.db.save(toAdd);
        toIndex.setId(toAdd.getId());
        toIndex.setSubject(indexCrypt.cryptStringWords(toIndex.getSubject()));
        indexer.add(toIndex);
        this.setResult(toAdd);
    }

}
