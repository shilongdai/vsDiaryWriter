package net.viperfish.journal.archieveDB;

import java.io.File;

public class TarArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new TarArchiveEntryDatabase(archiveFile);
	}

}
