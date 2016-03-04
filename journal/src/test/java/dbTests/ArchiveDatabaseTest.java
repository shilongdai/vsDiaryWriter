package test.java.dbTests;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.archieveDB.ArchiveEntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.file.CommonFunctions;

public abstract class ArchiveDatabaseTest extends DatabaseTest {

	protected abstract ArchiveEntryDatabase getADB(File archiveFile);

	@Override
	protected ArchiveEntryDatabase getDB(File dataDir) {
		CommonFunctions.initDir(dataDir);
		File fileLocat = new File(dataDir, "archive");
		return getADB(fileLocat);
	}

	@Test
	public void testPersist() {
		ArchiveEntryDatabase database = getDB(new File("test"));
		for (int i = 0; i < 10; ++i) {
			Journal j = new Journal();
			j.setSubject("testPersist");
			j.setContent("testPersist");
			database.addEntry(j);
		}
		database.flush();
		database = getDB(new File("test"));
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
