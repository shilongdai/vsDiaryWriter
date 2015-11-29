package net.viperfish.journal.persistent;

import java.util.List;

import net.viperfish.journal.framework.Journal;

public interface EntryDatabase {

	Journal addEntry(Journal j);

	Journal removeEntry(Long id);

	Journal getEntry(Long id);

	Journal updateEntry(Long id, Journal j);

	List<Journal> getAll();

	void clear();

}
