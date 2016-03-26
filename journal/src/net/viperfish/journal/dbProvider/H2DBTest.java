package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.DatabaseTest;
import net.viperfish.journal.framework.EntryDatabase;

final public class H2DBTest extends DatabaseTest {

	private H2EntryDatabase db;

	@Override
	protected EntryDatabase getDB(File dataDir) {
		if (db == null) {
			db = new H2EntryDatabase(dataDir);
		}
		return db;
	}

}
