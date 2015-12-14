package net.viperfish.journal.secure;

import java.io.File;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class SecureFactoryWrapper implements DataSourceFactory {

	private DataSourceFactory factory;
	private EntryDatabase db;
	private String password;

	public SecureFactoryWrapper(DataSourceFactory toWrap, String password) {
		this.factory = toWrap;
		this.password = password;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		if (db == null) {
			db = new SecureEntryDatabaseWrapper(factory.createDatabaseObject(),
					password);
		}
		return db;
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDataDirectory(File dir) {
		factory.setDataDirectory(dir);

	}

}
