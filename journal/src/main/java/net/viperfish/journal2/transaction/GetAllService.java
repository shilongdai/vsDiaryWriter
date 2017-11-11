package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.LinkedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;

final class GetAllService extends Service<Collection<Journal>> {

    private CrudRepository<Journal, ?> db;

    GetAllService(CrudRepository<Journal, ?> db) {
        super();
        this.db = db;
    }

    @Override
    protected Task<Collection<Journal>> createTask() {
        return new Task<Collection<Journal>>() {
            @Override
            protected Collection<Journal> call() throws Exception {
                LinkedList<Journal> result = new LinkedList<>();
                synchronized (Lock.class) {
                    for (Journal j : db.findAll()) {
                        result.add(j);
                    }
                }
                return result;
            }
        };
    }

}
