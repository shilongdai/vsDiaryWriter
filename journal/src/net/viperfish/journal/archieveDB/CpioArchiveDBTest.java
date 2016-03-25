package net.viperfish.journal.archieveDB;

import java.io.File;

public class CpioArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new CpioArchiveEntryDatabase(archiveFile);
	}

}
