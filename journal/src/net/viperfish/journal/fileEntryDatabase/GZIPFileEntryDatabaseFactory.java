package net.viperfish.journal.fileEntryDatabase;

import java.io.File;
import java.io.IOException;

import net.viperfish.utils.file.GZIPIOStreamHandler;
import net.viperfish.utils.file.IOFile;

public class GZIPFileEntryDatabaseFactory extends FileEntryDatabaseFactory {

	@Override
	protected IOFile createIOFile(File dataFile) {
		try {
			File resultFile = new File(dataFile.getCanonicalPath() + ".gz");
			return new IOFile(resultFile, new GZIPIOStreamHandler());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
