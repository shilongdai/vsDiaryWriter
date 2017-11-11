package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;

final class GetJournalService extends Service<Journal> {

    private CrudRepository<Journal, Long> db;
    private Long id;

    public GetJournalService(Long id, CrudRepository<Journal, Long> db) {
        this.id = id;
        this.db = db;
    }

    @Override
    protected Task<Journal> createTask() {
        return new Task<Journal>() {
            @Override
            protected Journal call() throws Exception {
                synchronized (Lock.class) {
                    Journal result = db.findOne(id);
                    return result;
                }
            }
        };
    }

}
