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
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;

class CpioArchiveEntryDatabase extends ArchiveEntryDatabase {

	public CpioArchiveEntryDatabase(File archiveFile) {
		super(archiveFile);
	}

	@Override
	protected ArchiveOutputStream getArchiveOut(File f) throws IOException {
		return new CpioArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
	}

	@Override
	protected ArchiveInputStream getArchiveIn(File f) throws IOException {
		return new CpioArchiveInputStream(new BufferedInputStream(new FileInputStream(f)));
	}

	@Override
	protected ArchiveEntry newEntry(String name, int length) {
		return new CpioArchiveEntry(name, length);
	}

}
