package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.DatabaseTest;
import net.viperfish.journal.framework.EntryDatabase;

public final class DerbyEntryDatabaseTest extends DatabaseTest {

	private DerbyEntryDatabase db;

	@Override
	protected EntryDatabase getDB(File dataDir) {
		if (db == null) {
			db = new DerbyEntryDatabase(dataDir);
		}
		return db;
	}

}
