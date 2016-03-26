package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.DatabaseTest;
import net.viperfish.journal.framework.EntryDatabase;

public final class HSQLDBTest extends DatabaseTest {

	private HSQLHibernateEntryDatabase db;

	@Override
	protected EntryDatabase getDB(File dataDir) {
		if (db == null) {
			db = new HSQLHibernateEntryDatabase(dataDir);
		}
		return db;

	}

}
