package net.viperfish.journal.persistent;

import java.io.File;

/**
 * a factory that will return an instance of a EntryDatabase
 * 
 * @author sdai
 * @see EntryDatabase
 */
public interface DataSourceFactory {

	/**
	 * get a dao object
	 * 
	 * @return a new or cached dao;
	 */
	public EntryDatabase getDatabaseObject();

	/**
	 * create a new dao object
	 * 
	 * @return the new dao
	 */
	public EntryDatabase createDatabaseObject();

	/**
	 * clean up
	 */
	public void cleanUp();

	/**
	 * set the data directory for the application
	 * 
	 * @param dir
	 *            the data directory
	 */
	public void setDataDirectory(File dir);
}
