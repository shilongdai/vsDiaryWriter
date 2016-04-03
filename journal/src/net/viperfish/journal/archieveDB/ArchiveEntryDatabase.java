package net.viperfish.journal.archieveDB;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;
import net.viperfish.utils.file.CommonFunctions;
import net.viperfish.utils.serialization.ObjectSerializer;

/**
 * an archive entry database that uses Apache common compression archive formats
 * to store entries. Sub classes should implement a specific format of an
 * archive for read and write.
 * 
 * <p>
 * This class is NOT thread safe
 * </p>
 * 
 * @author sdai
 *
 */
abstract class ArchiveEntryDatabase implements EntryDatabase {

	/**
	 * returns an {@link ArchiveOutputStream} for write.
	 * 
	 * This method returns an {@link ArchiveOutputStream} ready to write. This
	 * stream will be closed after every
	 * {@link ArchiveEntryDatabase#write(Journal[])}. It must match with
	 * {@link ArchiveEntryDatabase#getArchiveIn(File)} and
	 * {@link ArchiveEntryDatabase#newEntry(String, int)}.
	 * 
	 * In this implementation, the {@link ArchiveEntryDatabase#write(Journal[])}
	 * function uses this method to flush all entries into an archive.
	 * 
	 * @param f
	 *            the archive file. It is already created.
	 * @return the {@link ArchiveOutputStream} ready to write
	 * @throws IOException
	 *             if cannot create the {@link ArchiveOutputStream}
	 */
	protected abstract ArchiveOutputStream getArchiveOut(File f) throws IOException;

	/**
	 * returns an {@link ArchiveInputStream} ready to read
	 * 
	 * This method returns an {@link ArchiveInputStream} ready to read. This
	 * stream will be closed after every {@link ArchiveEntryDatabase#read()}. It
	 * must match with the {@link ArchiveEntryDatabase#getArchiveOut(File)} and
	 * {@link ArchiveEntryDatabase#newEntry(String, int)}.
	 * 
	 * In this implementation, the {@link ArchiveEntryDatabase#read()} method
	 * uses this stream to read all entries from an archive.
	 * 
	 * @param f
	 *            the archive file to read from
	 * @return the {@link ArchiveInputStream} ready to read
	 * @throws IOException
	 *             if cannot create the {@link ArchiveInputStream}
	 */
	protected abstract ArchiveInputStream getArchiveIn(File f) throws IOException;

	/**
	 * create a new {@link ArchiveEntry}
	 * 
	 * This method creates a new {@link ArchiveEntry} for reading and writing to
	 * individual entries. It must match with the
	 * {@link ArchiveEntryDatabase#getArchiveIn(File)} and
	 * {@link ArchiveEntryDatabase#getArchiveOut(File)}.
	 * 
	 * In this implementation, {@link ArchiveEntryDatabase#write(Journal[])}
	 * uses this to create {@link ArchiveEntry}.
	 * 
	 * @param name
	 *            the name of the archive entry
	 * @param length
	 *            the amount of byte will be written
	 * @return the new {@link ArchiveEntry} for I/O
	 */
	protected abstract ArchiveEntry newEntry(String name, int length);

	/**
	 * get the archive file
	 * 
	 * This method returns the archive file that the
	 * {@link ArchiveEntryDatabase} writes to and reads from.
	 * 
	 * @return the archive file. Matches with the file passed to
	 *         {@link ArchiveEntryDatabase#getArchiveIn(File)} and
	 *         {@link ArchiveEntryDatabase#getArchiveOut(File)}
	 */
	protected File getArchiveFile() {
		return archiveFile;
	}

	/**
	 * writes journals to archive file.
	 * 
	 * This method writes a list of journals to the archive file. It overwrites
	 * existing archive entries. The list of {@link Journal} are serialized.
	 * 
	 * @param j
	 *            the list of journals
	 * @throws FailToSyncEntryException
	 *             if cannot write to the archive file
	 */
	private void write(Journal[] j) throws FailToSyncEntryException {
		try {
			CommonFunctions.initFile(archiveFile);
		} catch (IOException e1) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot create file to write archive:" + e1.getMessage());
			f.initCause(e1);
			throw f;
		}
		ObjectSerializer<Journal> s = new ObjectSerializer<>(Journal.class);
		try (ArchiveOutputStream out = getArchiveOut(archiveFile)) {
			for (Journal i : j) {
				byte[] data = s.serialize(i);
				ArchiveEntry entry = newEntry(i.getId().toString(), data.length);
				out.putArchiveEntry(entry);
				out.write(data);
				out.closeArchiveEntry();
			}
			out.finish();
			out.flush();
		} catch (IOException e) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot write entries to archive:" + e.getMessage());
			f.initCause(e);
			throw f;
		}
	}

	/**
	 * reads all entries from an archive
	 * 
	 * This method reads all entries from the archive file, and de-serialize it
	 * to {@link Journal}
	 * 
	 * @return entries read
	 */
	private Journal[] read() throws FailToSyncEntryException {
		try {
			if (CommonFunctions.initFile(archiveFile)) {
				return new Journal[0];
			}
		} catch (IOException e1) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot create file to read archive:" + e1.getMessage());
			f.initCause(e1);
			throw f;
		}
		List<Journal> result = new LinkedList<>();
		ObjectSerializer<Journal> s = new ObjectSerializer<>(Journal.class);
		try (ArchiveInputStream in = getArchiveIn(archiveFile)) {
			ArchiveEntry entry = in.getNextEntry();
			while (entry != null) {
				byte[] buffer = IOUtils.toByteArray(in);
				result.add(s.deSerilize(buffer));
				entry = in.getNextEntry();
			}
			return result.toArray(new Journal[0]);
		} catch (IOException e) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot read entries from archive:" + e.getMessage());
			f.initCause(e);
			throw f;
		}
	}

	private long currentId;
	private final Map<Long, Journal> buffer;
	private final File archiveFile;
	private boolean isLoaded;

	/**
	 * creates an {@link ArchiveEntryDatabase} at the given location
	 * 
	 * This constructor creates an {@link ArchiveEntryDatabase} that is based on
	 * the file given.
	 * 
	 * @param archiveFile
	 *            the archive file to I/O
	 */
	public ArchiveEntryDatabase(File archiveFile) {
		buffer = new TreeMap<>();
		currentId = 0;
		this.archiveFile = archiveFile;
		isLoaded = false;
	}

	@Override
	public Journal addEntry(Journal j) {
		j.setId(currentId);
		buffer.put(currentId, j);
		currentId++;
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal removed = getEntry(id);
		buffer.remove(id);
		return removed;
	}

	@Override
	public Journal getEntry(Long id) {
		return buffer.get(id);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		removeEntry(id);
		j.setId(id);
		return addEntry(j);
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Entry<Long, Journal> i : buffer.entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public void clear() {
		buffer.clear();

	}

	/**
	 * write all entries in memory to the archive
	 * 
	 * This method flushes all {@link Journal} to the archive file specified in
	 * {@link ArchiveEntryDatabase#ArchiveEntryDatabase(File)}
	 */
	public void flush() {
		try {
			write(getAll().toArray(new Journal[0]));
		} catch (FailToSyncEntryException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * load all entries from an archive into memory
	 * 
	 * This method loads all {@link Journal}s in the archive file specified in
	 * {@link ArchiveEntryDatabase#ArchiveEntryDatabase(File)} into memory.
	 */
	public void load() {

		if (isLoaded) {
			return;
		}

		Journal[] result = new Journal[0];
		// portion of possible failure
		try {
			result = read();
		} catch (FailToSyncEntryException e) {
			throw new RuntimeException(e);
		}

		long max = 0;
		for (Journal i : result) {
			buffer.put(i.getId(), i);
			if (i.getId() > max) {
				max = i.getId();
			}
		}

		isLoaded = true;
		this.currentId = max + 1;
	}

}
