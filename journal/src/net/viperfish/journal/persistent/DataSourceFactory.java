package net.viperfish.journal.persistent;

import java.io.File;

/**
 * a factory that will return an instance of a EntryDatabase
 * 
 * @author sdai
 * @see EntryDatabase
 */
public interface DataSourceFactory {
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
