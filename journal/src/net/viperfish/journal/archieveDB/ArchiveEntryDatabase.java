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
import net.viperfish.utils.file.CommonFunctions;
import net.viperfish.utils.serialization.ObjectSerializer;

public abstract class ArchiveEntryDatabase implements EntryDatabase {

	protected abstract ArchiveOutputStream getArchiveOut(File f) throws IOException;

	protected abstract ArchiveInputStream getArchiveIn(File f) throws IOException;

	protected abstract ArchiveEntry newEntry(String name, int length);

	protected File getArchiveFile() {
		return archiveFile;
	}

	private void write(Journal[] j) {
		try {
			CommonFunctions.initFile(archiveFile);
		} catch (IOException e1) {
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
			throw new RuntimeException(e);
		}
	}

	private Journal[] read() {
		try {
			if (CommonFunctions.initFile(archiveFile)) {
				return new Journal[0];
			}
		} catch (IOException e1) {
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
			throw new RuntimeException(e);
		}
	}

	private long currentId;
	private Map<Long, Journal> buffer;
	private File archiveFile;
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

	public void flush() {
		write(getAll().toArray(new Journal[0]));
	}

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
