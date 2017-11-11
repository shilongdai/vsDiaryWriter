package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;

final class RemoveJournalService extends Service<Journal> {

    private CrudRepository<Journal, Long> db;
    private Long id;

    public RemoveJournalService(Long id, CrudRepository<Journal, Long> db) {
        this.db = db;
        this.id = id;
    }

    @Override
    protected Task<Journal> createTask() {
        return new Task<Journal>() {
            @Override
            protected Journal call() throws Exception {
                synchronized (Lock.class) {
                    Journal toRemove = db.findOne(id);
                    db.delete(id);
                    return toRemove;
                }
            }
        };
    }

}
