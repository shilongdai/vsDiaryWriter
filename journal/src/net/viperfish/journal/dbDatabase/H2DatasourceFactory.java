package net.viperfish.journal.dbDatabase;

import java.io.File;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class H2DatasourceFactory implements DataSourceFactory {

	private File dataDir;
	private H2EntryDatabase db;

	@Override
	public EntryDatabase createDatabaseObject() {
		if (db == null) {
			db = new H2EntryDatabase(dataDir);
		}
		return db;
	}

	@Override
	public void cleanUp() {
		db.closeSession();

	}

	@Override
	public void setDataDirectory(File dir) {
		this.dataDir = dir;

	}

}
