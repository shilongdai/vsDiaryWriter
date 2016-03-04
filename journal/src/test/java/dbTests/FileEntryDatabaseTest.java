package test.java.dbTests;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.dbProvider.FileEntryDatabase;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.file.CommonFunctions;
import net.viperfish.utils.file.GZIPIOStreamHandler;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

public class FileEntryDatabaseTest extends DatabaseTest {

	private FileEntryDatabase db;
	private File dir;

	public FileEntryDatabaseTest() {

	}

	@Override
	protected EntryDatabase getDB(File dataDir) {
		if (db == null) {
			this.dir = dataDir;
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
