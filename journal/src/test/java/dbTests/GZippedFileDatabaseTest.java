package test.java.dbTests;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.fileDatabase.GZippedFileEntryDatabase;
import net.viperfish.journal.persistent.EntryDatabase;

public class GZippedFileDatabaseTest extends DatabaseTest {
	private EntryDatabase db;

	@Override
	protected EntryDatabase getDB() {
		File testDir = new File("test");
		if (!testDir.exists()) {
			testDir.mkdir();
		}
		try {
			if (db == null) {
				db = new GZippedFileEntryDatabase(new File(
						testDir.getCanonicalPath() + "/testData"));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}

}
