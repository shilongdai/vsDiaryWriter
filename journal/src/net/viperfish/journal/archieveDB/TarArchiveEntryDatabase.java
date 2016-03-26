package net.viperfish.journal.archieveDB;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

/**
 * EntryDatabase using Tar archive
 * 
 * @author sdai
 *
 */
final class TarArchiveEntryDatabase extends ArchiveEntryDatabase {

	public TarArchiveEntryDatabase(File archiveFile) {
		super(archiveFile);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ArchiveOutputStream getArchiveOut(File f) throws IOException {
		return new TarArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
	}

	@Override
	protected ArchiveInputStream getArchiveIn(File f) throws IOException {
		return new TarArchiveInputStream(new BufferedInputStream(new FileInputStream(f)));
	}

	@Override
	protected ArchiveEntry newEntry(String name, int length) {
		TarArchiveEntry entry = new TarArchiveEntry(name);
		entry.setSize(length);
		return entry;
	}

}
