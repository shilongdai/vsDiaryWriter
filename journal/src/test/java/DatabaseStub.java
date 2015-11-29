package test.java;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.EntryDatabase;

public class DatabaseStub implements EntryDatabase {

	private Map<Long, Journal> backend;
	private Long currentMax;

	public DatabaseStub() {
		backend = new TreeMap<Long, Journal>();
		currentMax = new Long(0);
	}

	@Override
	public Journal addEntry(Journal j) {
		j.setId(currentMax);
		backend.put(currentMax, j);
		currentMax = new Long(currentMax + 1);
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal deleted = getEntry(id);
		backend.remove(id);
		return deleted;
	}

	@Override
	public Journal getEntry(Long id) {
		return backend.get(id);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		Journal reference = backend.get(id);
		reference.setContent(j.getContent());
		reference.setDate(j.getDate());
		reference.setSubject(j.getSubject());
		return reference;
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<Journal>();
		for (Entry<Long, Journal> i : backend.entrySet()) {
			result.add(i.getValue());
		}
		return result;
	}

	@Override
	public void clear() {
		backend.clear();
		currentMax = 0L;
	}

}
