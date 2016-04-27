package net.viperfish.journal.dbProvider;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.framework.file.CommonFunctions;
import net.viperfish.framework.file.GZIPIOStreamHandler;
import net.viperfish.framework.file.IOFile;
import net.viperfish.framework.file.TextIOStreamHandler;
import net.viperfish.journal.framework.DatabaseTest;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;

public final class FileEntryDatabaseTest extends DatabaseTest {

	private FileEntryDatabase db;

	public FileEntryDatabaseTest() {

	}

	@Override
	protected EntryDatabase getDB(File dataDir) {
		if (db == null) {
			db = new FileEntryDatabase(new IOFile(new File(dataDir, "fileTest"), new GZIPIOStreamHandler()));
		}
		return db;
	}

	@Test
	public void testPersistence() {
		File dir = new File("test");
		FileEntryDatabase database = new FileEntryDatabase(
				new IOFile(new File(dir, "testPersist"), new TextIOStreamHandler()));
		for (int i = 0; i < 10; ++i) {
			Journal j = new Journal();
			j.setSubject("testPersist");
			j.setContent("testPersist");
			database.addEntry(j);
		}
		database.flush();
		database = new FileEntryDatabase(new IOFile(new File(dir, "testPersist"), new TextIOStreamHandler()));
		database.load();
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
