package test.java.dbTests;

import java.io.File;

import net.viperfish.journal.archieveDB.ArchiveEntryDatabase;
import net.viperfish.journal.archieveDB.ZipArchiveEntryDatabase;

public class ZipArchiveDBTest extends ArchiveDatabaseTest {

	@Override
	protected ArchiveEntryDatabase getADB(File archiveFile) {
		return new ZipArchiveEntryDatabase(archiveFile);
	}

}
