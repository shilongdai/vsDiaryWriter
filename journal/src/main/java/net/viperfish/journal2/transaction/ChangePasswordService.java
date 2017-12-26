package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.lang.model.type.NullType;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.Journal;

public class ChangePasswordService extends Service<NullType> {

    private String newPassword;
    private CryptedJournalDatabase db;
    private AuthenticationManager auth;

    public ChangePasswordService(String newPassword, CryptedJournalDatabase db, AuthenticationManager auth) {
        super();
        this.newPassword = newPassword;
        this.db = db;
        this.auth = auth;
    }

    @Override
    protected Task<NullType> createTask() {
        return new Task<NullType>() {

            @Override
            protected NullType call() throws Exception {
                Iterable<Journal> all = db.findAll();
                auth.setPassword(newPassword);
                db.save(all);
                return null;
            }
        };
    }

}
