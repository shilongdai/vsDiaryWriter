package net.viperfish.journal.fileDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.json.JsonGenerator;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public abstract class FileEntryDatabase implements EntryDatabase {

	private static class FileStructure {
		private Long nextID;
		private Journal[] data;

		public FileStructure() {
			nextID = new Long(0);
			data = null;
		}

		public Long getNextID() {
			return nextID;
		}

		public void setNextID(Long nextID) {
			this.nextID = nextID;
		}

		public Journal[] getData() {
			return data;
		}

		public void setData(Journal[] data) {
			this.data = data;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(data);
			result = prime * result
					+ ((nextID == null) ? 0 : nextID.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FileStructure other = (FileStructure) obj;
			if (!Arrays.equals(data, other.data))
				return false;
			if (nextID == null) {
				if (other.nextID != null)
					return false;
			} else if (!nextID.equals(other.nextID))
				return false;
			return true;
		}

	}

	private File fStorage;
	private JsonGenerator json;
	private Map<Long, Journal> buffer;
	private Long nextId;

	public void loadBuffer() {
		if (!fStorage.exists()) {
			try {
				fStorage.createNewFile();
				flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		String data = readFile(fStorage);
		if (data.length() == 0) {
			return;
		}
		try {
			FileStructure content = json.fromJson(FileStructure.class, data);
			this.nextId = content.getNextID();
			for (Journal i : content.getData()) {
				buffer.put(i.getId(), i);
			}
		} catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}

	}

	public void flush() {
		FileStructure struct = new FileStructure();
		struct.setNextID(nextId);
		List<Journal> buf = new LinkedList<Journal>();
		for (Entry<Long, Journal> i : buffer.entrySet()) {
			buf.add(i.getValue());
		}
		struct.setData(buf.toArray(new Journal[0]));
		try {
			String toWrite = json.toJson(struct);
			writeFile(fStorage, toWrite);
		} catch (JsonGenerationException | JsonMappingException e) {
			throw new RuntimeException(e);
		}

	}

	protected abstract String readFile(File path);

	protected abstract void writeFile(File path, String data);

	public FileEntryDatabase(File dataFile) {
		fStorage = dataFile;
		json = new JsonGenerator();
		buffer = new HashMap<Long, Journal>();
		nextId = new Long(0);
	}

	@Override
	public synchronized Journal addEntry(Journal j) {
		j.setId(nextId);
		buffer.put(nextId, j);
		nextId++;
		flush();
		return j;
	}

	@Override
	public synchronized Journal removeEntry(Long id) {
		Journal toDelete = getEntry(id);
		buffer.remove(id);
		flush();
		return toDelete;
	}

	@Override
	public synchronized Journal getEntry(Long id) {
		return buffer.get(id);
	}

	@Override
	public synchronized Journal updateEntry(Long id, Journal j) {
		j.setId(id);
		Journal stored = buffer.get(id);
		stored.setSubject(j.getSubject());
		stored.setContent(j.getContent());
		flush();
		return j;
	}

	@Override
	public synchronized List<Journal> getAll() {
		List<Journal> result = new LinkedList<Journal>();
		for (Entry<Long, Journal> i : buffer.entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public synchronized void clear() {
		buffer.clear();
		nextId = new Long(0);
		fStorage.delete();
	}

}
