package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

/**
 * a EntryDatabase factory that creates H2 based EntryDatabase
 * 
 * @author sdai
 *
 */
final class H2DatasourceFactory implements DataSourceFactory {

	private File dataDir;
	private H2EntryDatabase db;

	@Override
	public EntryDatabase createDatabaseObject() {
		db = new H2EntryDatabase(dataDir);
		return db;
	}

	@Override
	public void cleanUp() {
		if (db != null) {
			db.closeSession();
		}
		H2EntryDatabase.disposeShared();

	}

	@Override
	public void setDataDirectory(File dir) {
		this.dataDir = dir;

	}

	@Override
	public EntryDatabase getDatabaseObject() {
		if (db == null) {
			db = new H2EntryDatabase(dataDir);
		}
		return db;
	}

	@Override
	public void refresh() {
		db = null;

	}

}
