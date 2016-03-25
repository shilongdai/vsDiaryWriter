package net.viperfish.journal.dbProvider;

import java.io.File;

import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * creates Text File based EntryDatabase
 * 
 * @author sdai
 *
 */
class TextFileEntryDatabaseFactory extends FileEntryDatabaseFactory {

	@Override
	protected IOFile createIOFile(File dataFile) {
		return new IOFile(dataFile, new TextIOStreamHandler());
	}

}
