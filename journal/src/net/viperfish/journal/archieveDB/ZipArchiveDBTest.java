package net.viperfish.journal.archieveDB;

import java.io.File;

public class ZipArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new ZipArchiveEntryDatabase(archiveFile);
	}

}
