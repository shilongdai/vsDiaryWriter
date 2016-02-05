package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.dbProvider.H2EntryDatabase;
import net.viperfish.journal.framework.EntryDatabase;

public class H2DBTest extends DatabaseTest {

	private H2EntryDatabase db;

	@Override
	protected EntryDatabase getDB() {
		File test = new File("test");
		if (!test.exists()) {
			test.mkdir();
		}
		if (db == null) {
			db = new H2EntryDatabase(test);
		}
		return db;
	}

}
