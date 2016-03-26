package net.viperfish.journal.dbProvider;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.viperfish.journal.framework.Journal;

/**
 * the memory structure of journals in a FileEntryDatabase
 * 
 * @see FileEntryDatabase
 * @author sdai
 *
 */
class FileMemoryStructure implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8983675555496559303L;
	private Long id;
	private final Map<Long, Journal> data;

	public FileMemoryStructure() {
		id = new Long(0);
		data = new HashMap<>();
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

	public void incrementID() {
		id += 1;
	}

}
