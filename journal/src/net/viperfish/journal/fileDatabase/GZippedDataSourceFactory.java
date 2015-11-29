package net.viperfish.journal.fileDatabase;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;

public class GZippedDataSourceFactory implements DataSourceFactory {

	private GZippedFileEntryDatabase db;

	public GZippedDataSourceFactory() {
		db = new GZippedFileEntryDatabase();
		db.loadBuffer();
	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return db;
	}

	@Override
	public void cleanUp() {

	}

}
