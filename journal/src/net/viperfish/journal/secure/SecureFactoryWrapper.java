package net.viperfish.journal.secure;

import java.io.File;
import java.util.Properties;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class SecureFactoryWrapper implements DataSourceFactory {

	private DataSourceFactory factory;
	private EntryDatabase db;
	private Properties config;

	public SecureFactoryWrapper(DataSourceFactory toWrap, Properties p) {
		this.factory = toWrap;
		config = p;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		if (db == null) {
			db = new SecureEntryDatabaseWrapper(factory.createDatabaseObject(),
					config);
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
