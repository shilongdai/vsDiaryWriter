package net.viperfish.journal2.transaction;

import java.util.HashMap;

import javax.lang.model.type.NullType;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.Journal;

public final class ResyncCryptService extends Service<NullType> {
	private CryptedJournalDatabase db;

	public ResyncCryptService(CryptedJournalDatabase db) {
		super();
		this.db = db;
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
					db.save(all);
				}
				return null;
			}
		};
	}

}
