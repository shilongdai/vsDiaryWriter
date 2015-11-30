package net.viperfish.journal.persistent;

import java.io.File;

public interface DataSourceFactory {
	public EntryDatabase createDatabaseObject();

	public void cleanUp();

	public void setDataDirectory(File dir);
}
