package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;

final class SaveJournalService extends Service<Journal> {

    private Journal toAdd;
    private CrudRepository<Journal, ?> db;

    public SaveJournalService(Journal toAdd, CrudRepository<Journal, ?> db) {
        this.toAdd = toAdd;
        this.db = db;
    }

    @Override
    protected Task<Journal> createTask() {
        return new Task<Journal>() {
            @Override
            protected Journal call() throws Exception {
                synchronized (Lock.class) {
                    Journal added = db.save(toAdd);;
                    return added;
                }
            }
        };
    }

}
