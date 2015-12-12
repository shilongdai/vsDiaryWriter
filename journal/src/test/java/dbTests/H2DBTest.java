package test.java.dbTests;

import java.io.File;

import net.viperfish.dbDatabase.H2EntryDatabase;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.config.Configuration;

public class H2DBTest extends DatabaseTest {

	private H2EntryDatabase db;

	@Override
	protected EntryDatabase getDB() {
		Configuration.defaultAll();
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
