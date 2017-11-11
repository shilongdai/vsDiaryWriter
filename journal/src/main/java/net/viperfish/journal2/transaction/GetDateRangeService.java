package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;

final class GetDateRangeService extends Service<Collection<Journal>> {

    private Date lower;
    private Date upper;
    private JournalDatabase db;

    GetDateRangeService(Date lower, Date upper, JournalDatabase db) {
        super();
        this.lower = lower;
        this.upper = upper;
        this.db = db;
    }

    @Override
    protected Task<Collection<Journal>> createTask() {
        return new Task<Collection<Journal>>() {
            @Override
            protected Collection<Journal> call() throws Exception {
                LinkedList<Journal> result = new LinkedList<>();
                synchronized (Lock.class) {
                    for (Journal j : db.findByTimestampBetween(lower, upper)) {
                        result.add(j);
                    }
                }
                return result;
            }
        };
    }

}
