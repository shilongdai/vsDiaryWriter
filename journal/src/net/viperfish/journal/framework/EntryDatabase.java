package net.viperfish.journal.framework;

import java.util.List;

/**
 * the interface that handles persistence for the application
 * 
 * @author sdai
 *
 */
public interface EntryDatabase {

	Journal addEntry(Journal j);

	Journal removeEntry(Long id);

	Journal getEntry(Long id);

	Journal updateEntry(Long id, Journal j);

	List<Journal> getAll();

	void clear();

}
