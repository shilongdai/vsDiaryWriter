package net.viperfish.journal.dbProvider;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.framework.file.CommonFunctions;
import net.viperfish.journal.framework.DatabaseTest;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;

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

	@Test
	public void testPersistence() {
		JavaSerializationEntryDatabase database = new JavaSerializationEntryDatabase();
		for (int i = 0; i < 10; ++i) {
			Journal j = new Journal();
			j.setSubject("testPersist");
			j.setContent("testPersist");
			database.addEntry(j);
		}
		JavaSerializationEntryDatabase.serialize(new File("testPersist"), database);
		database = JavaSerializationEntryDatabase.deSerialize(new File("testPersist"));
		int count = 0;
		for (Journal i : database.getAll()) {
			Assert.assertEquals("testPersist", i.getContent());
			Assert.assertEquals("testPersist", i.getSubject());
			count++;
		}
		Assert.assertEquals(10, count);
		CommonFunctions.delete(new File("testPersist"));
	}

}
