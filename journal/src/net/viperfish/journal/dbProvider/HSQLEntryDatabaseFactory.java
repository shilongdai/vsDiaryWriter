package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

final class HSQLEntryDatabaseFactory implements DataSourceFactory {

	private File dataDir;
	private HSQLHibernateEntryDatabase db;

	HSQLEntryDatabaseFactory() {
		dataDir = null;
		db = null;
	}

	@Override
	public EntryDatabase getDatabaseObject() {
		if (db == null) {
			db = new HSQLHibernateEntryDatabase(dataDir);
		}
		return db;
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return new HSQLHibernateEntryDatabase(dataDir);
	}

	@Override
	public void cleanUp() {
		if (db != null) {
			db.closeSession();
		}
		HSQLHibernateEntryDatabase.closeFactory();
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
