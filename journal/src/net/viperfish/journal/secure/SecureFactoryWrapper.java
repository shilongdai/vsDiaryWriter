package net.viperfish.journal.secure;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class SecureFactoryWrapper implements DataSourceFactory {

	private DataSourceFactory factory;
	private EntryDatabase db;

	public SecureFactoryWrapper(DataSourceFactory toWrap) {
		this.factory = toWrap;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		if (db == null) {
			db = new SecureEntryDatabaseWrapper(factory.createDatabaseObject());
		}
		return db;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

}
