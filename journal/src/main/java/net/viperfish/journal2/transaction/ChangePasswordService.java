package net.viperfish.journal2.transaction;

import java.util.HashMap;

import javax.lang.model.type.NullType;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.CryptoInfo;
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
				synchronized (Lock.class) {
					Iterable<Journal> all = db.findAll();
					for (Journal i : all) {
						i.setInfoMapping(new HashMap<String, CryptoInfo>());
					}
					auth.setPassword(newPassword);
					db.save(all);
				}
				return null;
			}
		};
	}

}
