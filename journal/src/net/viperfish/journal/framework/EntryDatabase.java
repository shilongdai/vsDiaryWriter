package net.viperfish.journal.framework;

import java.util.List;

/**
 * the interface that handles persistence for the application
 * 
 * @author sdai
 *
 */
public interface EntryDatabase {

	/**
	 * stores a journal, and fills in the id
	 * 
	 * @param j
	 *            the journal to store
	 * @return the stored journal, with id filled
	 */
	Journal addEntry(Journal j);

	/**
	 * removes a journal
	 * 
	 * @param id
	 *            the id of the journal to remove
	 * @return the removed journal, or null if not found
	 */
	Journal removeEntry(Long id);

	/**
	 * gets a journal
	 * 
	 * @param id
	 *            the id of the journal to get
	 * @return the journal or null if not found
	 */
	Journal getEntry(Long id);

	/**
	 * updates a journal
	 * 
	 * @param id
	 *            the id of the journal to update
	 * @param j
	 *            the new version of the journal
	 * @return the updated journal
	 */
	Journal updateEntry(Long id, Journal j);

	/**
	 * get all journals
	 * 
	 * @return all of the journals
	 */
	List<Journal> getAll();

	/**
	 * clear all journals
	 */
	void clear();

}
