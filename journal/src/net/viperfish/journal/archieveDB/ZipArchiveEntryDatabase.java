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
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * EntryDatabase using Zip archive
 * 
 * @author sdai
 *
 */
class ZipArchiveEntryDatabase extends ArchiveEntryDatabase {

	public ZipArchiveEntryDatabase(File archiveFile) {
		super(archiveFile);
	}

	@Override
	protected ArchiveOutputStream getArchiveOut(File f) throws IOException {
		return new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
	}

	@Override
	protected ArchiveInputStream getArchiveIn(File f) throws IOException {
		return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(f)));
	}

	@Override
	protected ArchiveEntry newEntry(String name, int length) {
		ZipArchiveEntry entry = new ZipArchiveEntry(name);
		entry.setSize(length);
		return entry;
	}

}
