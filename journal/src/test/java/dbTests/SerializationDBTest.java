package test.java.dbTests;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.dbProvider.JavaSerializationEntryDatabase;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.utils.file.CommonFunctions;

public class SerializationDBTest extends DatabaseTest {

	private JavaSerializationEntryDatabase db;

	@Override
	protected EntryDatabase getDB(File dataDir) {
		File f = new File(dataDir, "javaDBS");
		try {
			CommonFunctions.initFile(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (db == null) {
			db = new JavaSerializationEntryDatabase();
		}
		return db;
	}

}
