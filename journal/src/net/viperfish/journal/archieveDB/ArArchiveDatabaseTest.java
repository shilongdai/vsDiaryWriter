package net.viperfish.journal.archieveDB;

import java.io.File;

public class ArArchiveDatabaseTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new ArArchiveEntryDatabase(archiveFile);
	}

}
