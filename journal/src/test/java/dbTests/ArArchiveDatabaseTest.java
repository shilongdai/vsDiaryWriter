package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.archieveDB.ArArchiveEntryDatabase;
import net.viperfish.journal.archieveDB.ArchiveEntryDatabase;
import net.viperfish.utils.file.CommonFunctions;

public class ArArchiveDatabaseTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getDB(File dataDir) {
		CommonFunctions.initDir(dataDir);
		File fileLocat = new File(dataDir, "archive");
		return new ArArchiveEntryDatabase(fileLocat);
	}

}
