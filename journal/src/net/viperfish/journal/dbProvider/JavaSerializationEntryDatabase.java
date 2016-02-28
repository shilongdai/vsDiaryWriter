package net.viperfish.journal.dbProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.file.CommonFunctions;

public class JavaSerializationEntryDatabase implements EntryDatabase, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3427237046238186820L;
	private Map<Long, Journal> data;
	private Long id;

	public JavaSerializationEntryDatabase() {
		id = new Long(0);
		data = new HashMap<>();
	}

	@Override
	public Journal addEntry(Journal j) {
		j.setId(id);
		data.put(id, j);
		id++;
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal j = new Journal(data.get(id));
		data.remove(id);
		return j;
	}

	@Override
	public Journal getEntry(Long id) {
		return data.get(id);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		removeEntry(id);
		j.setId(id);
		data.put(id, j);
		return j;
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Entry<Long, Journal> i : data.entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public void clear() {
		data.clear();
		id = new Long(0);

	}

	public static void serialize(File serializedFile, JavaSerializationEntryDatabase db) {
		try {
			CommonFunctions.initFile(serializedFile);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		try (ObjectOutputStream out = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(serializedFile)))) {

			out.writeObject(db);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static JavaSerializationEntryDatabase deSerialize(File serializedFile) {
		try {
			CommonFunctions.initFile(serializedFile);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		try (ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(serializedFile)))) {
			return (JavaSerializationEntryDatabase) in.readObject();
		} catch (EOFException e) {
			return new JavaSerializationEntryDatabase();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

	}

}
