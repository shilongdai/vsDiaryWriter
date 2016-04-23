package net.viperfish.journal.framework;

import java.util.List;

import net.viperfish.journal.framework.errors.FailToSyncEntryException;

/**
 * A persistence object that stored {@link Journal}s.
 * 
 * This class <b>DOES NOT</b> have to be thread safe.
 * 
 * @author sdai
 *
 */
public interface EntryDatabase {

	/**
	 * stores a {@link Journal}
	 * 
	 * This method stores a {@link Journal} and fills in its id.
	 * 
	 * @param j
	 *            the journal to store
	 * @return the stored journal, with id filled
	 * 
	 * @throws FailToSyncEntryException
	 *             if cannot sync the new entry with the storage
	 */
	Journal addEntry(Journal j) throws FailToSyncEntryException;

	/**
	 * removes a journal
	 * 
	 * @param id
	 *            the id of the journal to remove
	 * @return the removed journal, or null if not found
	 * 
	 * @throws FailToSyncEntryException
	 *             if cannot remove the entry from the storage
	 */
	Journal removeEntry(Long id) throws FailToSyncEntryException;

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
	 * This method updates the journal that has the given id.
	 * 
	 * @param id
	 *            the id of the journal to update
	 * @param j
	 *            the new version of the journal
	 * @return the updated journal
	 * 
	 * @throws FailToSyncEntryException
	 *             if cannot update the entry in the storage
	 */
	Journal updateEntry(Long id, Journal j) throws FailToSyncEntryException;

	/**
	 * get all journals
	 * 
	 * @return all of the journals
	 */
	List<Journal> getAll();

	/**
	 * clear all journals
	 * 
	 * @throws FailToSyncEntryException
	 *             if cannot clear entries
	 */
	void clear() throws FailToSyncEntryException;

}
