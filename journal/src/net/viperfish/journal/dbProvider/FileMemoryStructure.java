package net.viperfish.journal.dbProvider;

import java.util.Map;
import java.util.TreeMap;

import net.viperfish.journal.framework.Journal;

public class FileMemoryStructure {
	private Long id;
	private Map<Long, Journal> data;

	public FileMemoryStructure() {
		id = new Long(0);
		data = new TreeMap<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<Long, Journal> getData() {
		return data;
	}

	public void setData(Map<Long, Journal> data) {
		this.data = data;
	}

	public void incrementID() {
		id += 1;
	}

}
