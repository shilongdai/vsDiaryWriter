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
 * archive.
 * 
 * @author sdai
 *
 */
abstract class ArchiveEntryDatabase implements EntryDatabase {

	/**
	 * this should return an Apache common compress archive output stream ready
	 * to write. Used to write entries into archive. Should match with the
	 * {@link ArchiveEntryDatabase#getArchiveIn(File)}
	 * 
	 * @param f
	 *            the archive file
	 * @return the archive output stream ready to write
	 * @throws IOException
	 *             if failed to create archive output stream
	 */
	protected abstract ArchiveOutputStream getArchiveOut(File f) throws IOException;

	/**
	 * this should return an Apache common compress archive ready to read. Used
	 * to read entries from archive. It should match with the
	 * {@link ArchiveEntryDatabase#getArchiveOut(File)}
	 * 
	 * @param f
	 *            the archive file
	 * @return the archive input stream ready to read
	 * @throws IOException
	 *             if failed to create an archive input stream
	 */
	protected abstract ArchiveInputStream getArchiveIn(File f) throws IOException;

	/**
	 * this should create an Apache common compress archive entry ready to be
	 * used. The entry format should match with the
	 * {@link ArchiveEntryDatabase#getArchiveIn(File)} and the
	 * {@link ArchiveEntryDatabase#getArchiveOut(File)}
	 * 
	 * @param name
	 *            the name of the entry
	 * @param length
	 *            the number of bytes
	 * @return the archive entry ready to be used
	 */
	protected abstract ArchiveEntry newEntry(String name, int length);

	/**
	 * get the specified archive file
	 * 
	 * @return the archive file
	 */
	protected File getArchiveFile() {
		return archiveFile;
	}

	/**
	 * write entries into the archive file. The archive entry would then contain
	 * the serialized {@link Journal} Object. Overwrites existing entries.
	 * 
	 * @param j
	 *            the journals to write
	 */
	private void write(Journal[] j) {
		try {
			CommonFunctions.initFile(archiveFile);
		} catch (IOException e1) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot create file to write archive:" + e1.getMessage());
			f.initCause(e1);
			throw new RuntimeException(e1);
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
			throw new RuntimeException(f);
		}
	}

	/**
	 * read all entries from an archive
	 * 
	 * @return the entries
	 */
	private Journal[] read() {
		try {
			if (CommonFunctions.initFile(archiveFile)) {
				return new Journal[0];
			}
		} catch (IOException e1) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot create file to read archive:" + e1.getMessage());
			f.initCause(e1);
			throw new RuntimeException(e1);
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
			throw new RuntimeException(f);
		}
	}

	private long currentId;
	private final Map<Long, Journal> buffer;
	private final File archiveFile;
	private boolean isLoaded;

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
	 */
	public void flush() {
		write(getAll().toArray(new Journal[0]));
	}

	/**
	 * load all entries from an archive into memory
	 */
	public void load() {
		if (isLoaded) {
			return;
		}
		isLoaded = true;
		long max = 0;
		for (Journal i : read()) {
			buffer.put(i.getId(), i);
			if (i.getId() > max) {
				max = i.getId();
			}
		}
		this.currentId = max + 1;
	}

}
