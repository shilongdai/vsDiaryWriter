package net.viperfish.journal.fileDatabase;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class GZippedDataSourceFactory implements DataSourceFactory {

	private GZippedFileEntryDatabase db;
	private File dataFile;

	public GZippedDataSourceFactory() {
		dataFile = new File("data/journalEntries");
		db = new GZippedFileEntryDatabase(dataFile);
		db.loadBuffer();
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return db;
	}

	@Override
	public void cleanUp() {

	}

	@Override
	public void setDataDirectory(File dir) {
		try {
			dataFile = new File(dir.getCanonicalPath() + "/journalEntries");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}
