package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.journal.framework.EntryDatabase;

/**
 * a factory that creates or caches {@link EntryDatabase}.
 * 
 * @author sdai
 * @see EntryDatabase
 */
interface DataSourceFactory {

	/**
	 * get a dao object
	 * 
	 * This method should return a new {@link EntryDatabase} or a pre-existing,
	 * caches {@link EntryDatabase} that is usable.
	 * 
	 * @return a new or cached dao;
	 */
	public EntryDatabase getDatabaseObject();

	/**
	 * create a new dao object
	 * 
	 * This method should create a completely new usable {@link EntryDatabase}.
	 * 
	 * @return the new dao
	 */
	public EntryDatabase createDatabaseObject();

	/**
	 * clean up
	 * 
	 * This method should dispose all resources that this factory and its
	 * {@link EntryDatabase} uses.
	 */
	public void cleanUp();

	/**
	 * set the data directory for storing data
	 * 
	 * @param dir
	 *            the data directory
	 */
	public void setDataDirectory(File dir);

	/**
	 * refresh the EntryDatabase to reflect change in configuration or settings
	 * 
	 *
	 */
	public void refresh();
}
