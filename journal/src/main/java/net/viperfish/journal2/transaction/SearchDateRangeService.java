package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;

final class SearchDateRangeService extends Service<Collection<Journal>> {

    private JournalDatabase db;
    private String keyword;
    private Date lower;
    private Date upper;

    SearchDateRangeService(JournalDatabase db, String keyword, Date lower, Date upper) {
        super();
        this.db = db;
        this.keyword = keyword;
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    protected Task<Collection<Journal>> createTask() {
        return new Task<Collection<Journal>>() {
            @Override
            protected Collection<Journal> call() throws Exception {
                LinkedList<Journal> result = new LinkedList<>();
                String[] words = keyword.split(" ");
                synchronized (Lock.class) {
                    for (Journal j : db.findBySubjectAndTimestampBetween(words, lower, upper)) {
                        result.add(j);
                    }
                }
                return result;
            }
        };
    }

}
