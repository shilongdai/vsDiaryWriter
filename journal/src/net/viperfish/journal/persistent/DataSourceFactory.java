package net.viperfish.journal.persistent;

public interface DataSourceFactory {
	public EntryDatabase createDatabaseObject();

	public void cleanUp();
}
