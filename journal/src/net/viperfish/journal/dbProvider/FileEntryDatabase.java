package net.viperfish.journal.dbProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import net.viperfish.framework.file.IOFile;
import net.viperfish.framework.serialization.JsonGenerator;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;

/**
 * an EntryDatabase that is based on a file for persistent, does not flush until
 * flush is called
 * 
 * @author sdai
 *
 */
final class FileEntryDatabase implements EntryDatabase {

	private final IOFile file;
	private FileMemoryStructure struct;

	FileEntryDatabase(IOFile ioFile) {
		this.file = ioFile;
		struct = new FileMemoryStructure();
	}

	@Override
	public Journal addEntry(Journal j) {
		j.setId(struct.getId());
		struct.getData().put(j.getId(), j);
		struct.incrementID();
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal j = new Journal(struct.getData().get(id));
		struct.getData().remove(id);
		return j;
	}

	@Override
	public Journal getEntry(Long id) {
		return struct.getData().get(id);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		removeEntry(id);
		j.setId(id);
		struct.getData().put(id, j);
		return j;
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Entry<Long, Journal> i : struct.getData().entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public void clear() {
		struct.getData().clear();
		struct.setId(new Long(0));
	}

	/**
	 * flush the data into the file in JSON format
	 */
	public synchronized void flush() {
		JsonGenerator generator = new JsonGenerator();
		try {
			String toWrite = generator.toJson(struct);
			file.write(toWrite, StandardCharsets.UTF_16);
		} catch (IOException e) {
			FailToSyncEntryException e1 = new FailToSyncEntryException(
					"Cannot flush entries to file at:" + file.getFile() + " message:" + e.getMessage());
			e1.initCause(e);
			throw new RuntimeException(e1);
		}
	}

	/**
	 * load the data from the file in JSON format
	 */
	public synchronized void load() {
		try {
			String buf = file.read(StandardCharsets.UTF_16);
			if (buf.length() == 0) {
				return;
			}
			JsonGenerator generator = new JsonGenerator();
			struct = generator.fromJson(FileMemoryStructure.class, buf);
		} catch (IOException e) {
			FailToSyncEntryException f = new FailToSyncEntryException(
					"Cannot load entries from file:" + file.getFile() + " message:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(f);
		}
	}

}
