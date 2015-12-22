package net.viperfish.journal.fileEntryDatabase;

import java.io.File;

import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

public class TextFileEntryDatabaseFactory extends FileEntryDatabaseFactory {

	@Override
	protected IOFile createIOFile(File dataFile) {
		return new IOFile(dataFile, new TextIOStreamHandler());
	}

}
