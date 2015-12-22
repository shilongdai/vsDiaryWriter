package test.java.dbTests;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.fileEntryDatabase.FileEntryDatabase;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.file.GZIPIOStreamHandler;
import net.viperfish.utils.file.IOFile;

public class FileEntryDatabaseTest extends DatabaseTest {

	private final FileEntryDatabase db;

	public FileEntryDatabaseTest() {
		File test = new File("test");
		if (!test.exists()) {
			test.mkdir();
		}
		try {
			db = new FileEntryDatabase(new IOFile(new File(
					test.getCanonicalPath() + "/test"),
					new GZIPIOStreamHandler()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected EntryDatabase getDB() {
		return db;
	}

}
