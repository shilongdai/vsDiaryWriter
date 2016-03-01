package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.dbProvider.FileEntryDatabase;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.utils.file.GZIPIOStreamHandler;
import net.viperfish.utils.file.IOFile;

public class FileEntryDatabaseTest extends DatabaseTest {

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

}
