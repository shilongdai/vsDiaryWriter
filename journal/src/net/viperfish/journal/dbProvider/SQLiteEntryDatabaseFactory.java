package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

final class SQLiteEntryDatabaseFactory implements DataSourceFactory {

	private File dataDir;
	private SQLiteEntryDatabase db;

	SQLiteEntryDatabaseFactory() {
		dataDir = null;
		db = null;
	}

	@Override
	public EntryDatabase getDatabaseObject() {
		if (db == null) {
			db = new SQLiteEntryDatabase(dataDir);
		}
		return db;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return new SQLiteEntryDatabase(dataDir);
	}

	@Override
	public void cleanUp() {
		if (db != null) {
			db.closeSession();
		}
		SQLiteEntryDatabase.closeFactory();
	}

	@Override
	public void setDataDirectory(File dir) {
		this.dataDir = dir;

	}

	@Override
	public void refresh() {
		if (db != null) {
			db.closeSession();
			db = null;
		}

	}

}
