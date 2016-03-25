package net.viperfish.journal.archieveDB;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;

class ArArchiveEntryDatabase extends ArchiveEntryDatabase {

	public ArArchiveEntryDatabase(File archiveFile) {
		super(archiveFile);
	}

	@Override
	protected ArchiveOutputStream getArchiveOut(File f) throws FileNotFoundException {
		return new ArArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
	}

	@Override
	protected ArchiveInputStream getArchiveIn(File f) throws FileNotFoundException {
		return new ArArchiveInputStream(new BufferedInputStream(new FileInputStream(f)));
	}

	@Override
	protected ArchiveEntry newEntry(String name, int length) {
		return new ArArchiveEntry(name, length);
	}

}
