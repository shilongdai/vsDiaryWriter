package net.viperfish.journal.dbProvider;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import net.viperfish.journal.framework.EntryDatabase;

/**
 * a EntryDatabase factory that creates JavaSerialization EntryDatabase
 * 
 * @author sdai
 *
 */
final class JSerializationDataSourceFactory implements DataSourceFactory {

	private File sFile;
	private JavaSerializationEntryDatabase db;
	private Timer executer;

	public JSerializationDataSourceFactory() {
		executer = new Timer("flusher");
	}

	private void lazyLoadDB() {
		if (db == null) {
			db = JavaSerializationEntryDatabase.deSerialize(sFile);
			executer.schedule(new TimerTask() {

				@Override
				public void run() {
					JavaSerializationEntryDatabase.serialize(sFile, db);

				}
			}, 0, 60000);
		}
	}

	@Override
	public EntryDatabase getDatabaseObject() {
		lazyLoadDB();
		return db;

	}

	@Override
	public EntryDatabase createDatabaseObject() {
		return new JavaSerializationEntryDatabase();
	}

	@Override
	public void cleanUp() {
		if (db != null) {
			JavaSerializationEntryDatabase.serialize(sFile, db);
		}
		executer.cancel();

	}

	@Override
	public void setDataDirectory(File dir) {
		this.sFile = new File(dir, "journalEntrySerialized");
	}

	@Override
	public void refresh() {
		if (executer != null) {
			executer.cancel();
		}
		if (db != null) {
			JavaSerializationEntryDatabase.serialize(sFile, db);
			db = null;
		}

	}

}
