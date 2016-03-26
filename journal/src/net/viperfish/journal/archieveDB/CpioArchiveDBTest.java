package net.viperfish.journal.archieveDB;

import java.io.File;

public final class CpioArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new CpioArchiveEntryDatabase(archiveFile);
	}

}
