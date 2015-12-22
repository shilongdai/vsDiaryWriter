package net.viperfish.journal.secure;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class SecureFactoryWrapper implements DataSourceFactory {

	private final DataSourceFactory factory;
	private EntryDatabase db;
	private final String password;
	private File dataDir;

	public SecureFactoryWrapper(DataSourceFactory toWrap, String password) {
		this.factory = toWrap;
		this.password = password;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		if (db == null) {
			try {
				db = new SecureEntryDatabaseWrapper(
						factory.createDatabaseObject(), password, new File(
								dataDir.getCanonicalPath() + "/salt"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return db;
	}

	@Override
	public void cleanUp() {
		this.factory.cleanUp();

	}

	@Override
	public void setDataDirectory(File dir) {
		this.dataDir = dir;
		factory.setDataDirectory(dir);

	}

}
