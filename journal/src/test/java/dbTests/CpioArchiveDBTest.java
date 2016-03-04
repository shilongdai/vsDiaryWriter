package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.archieveDB.ArchiveEntryDatabase;
import net.viperfish.journal.archieveDB.CpioArchiveEntryDatabase;

public class CpioArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new CpioArchiveEntryDatabase(archiveFile);
	}

}
