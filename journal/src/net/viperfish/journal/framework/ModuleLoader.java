package net.viperfish.journal.framework;

import java.io.File;

/**
 * loader that loads third party components into the application
 * 
 * @author sdai
 *
 */
public interface ModuleLoader {

	/**
	 * load EntryDatabases, Indexers, JournalTransformers, and Authentication
	 * Manager Providers into the application
	 * 
	 * @param baseDir
	 *            the dir containing the providers
	 */
	public void loadModules(File baseDir);

}
