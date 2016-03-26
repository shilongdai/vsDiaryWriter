package net.viperfish.journal.dbProvider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.viperfish.utils.file.GZIPIOStreamHandler;
import net.viperfish.utils.file.IOFile;

/**
 * a FileEntryDatabaseFactory that create a GZip compressed file for data
 * storage
 * 
 * @author sdai
 *
 */
final class GZIPFileEntryDatabaseFactory extends FileEntryDatabaseFactory {

	@Override
	protected IOFile createIOFile(File dataFile) {
		try {
			File resultFile = new File(dataFile.getCanonicalPath() + ".gz");
			IOFile f = new IOFile(resultFile, new GZIPIOStreamHandler());
			if (!resultFile.exists()) {
				f.write("", StandardCharsets.UTF_16);
			}
			return f;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
