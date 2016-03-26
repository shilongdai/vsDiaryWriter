package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

final class DerbyEntryDatabaseFactory implements DataSourceFactory {

	private DerbyEntryDatabase db;
	private File dataDir;

	DerbyEntryDatabaseFactory() {
	}

	@Override
	public EntryDatabase getDatabaseObject() {
		if (db == null) {
			db = new DerbyEntryDatabase(dataDir);
		}
		return db;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return new DerbyEntryDatabase(dataDir);
	}

	@Override
	public void cleanUp() {
		if (db != null) {
			db.closeSession();
		}
		DerbyEntryDatabase.closeFactory();

	}

	@Override
	public void setDataDirectory(File dir) {
		this.dataDir = dir;

	}

	@Override
	public void refresh() {
		if (db != null) {
			db.closeSession();
		}
		db = null;
	}

}
