package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.archieveDB.ArchiveEntryDatabase;
import net.viperfish.journal.archieveDB.TarArchiveEntryDatabase;

public class TarArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new TarArchiveEntryDatabase(archiveFile);
	}

}
