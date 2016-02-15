package net.viperfish.journal.dbProvider;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.utils.file.IOFile;

abstract class FileEntryDatabaseFactory implements DataSourceFactory {

	private FileEntryDatabase db;
	private File dataFile;
	private Timer executor;

	public FileEntryDatabaseFactory() {
		executor = new Timer("flusher");
	}

	/**
	 * create the IOFile for the FileEntryDatabase
	 * 
	 * @see IOFile
	 * @param dataFile
	 *            the path of the file
	 * @return the IOFile for IO operations
	 */
	protected abstract IOFile createIOFile(File dataFile);

	@Override
	public EntryDatabase createDatabaseObject() {
		db = new FileEntryDatabase(createIOFile(dataFile));
		db.load();
		// auto flush
		executor.schedule(new TimerTask() {

			@Override
			public void run() {
				db.flush();
			}
		}, 0, 60000);

		return db;
	}

	@Override
	public EntryDatabase getDatabaseObject() {
		if (db == null) {
			db = new FileEntryDatabase(createIOFile(dataFile));
			db.load();
			// auto flush
			executor.schedule(new TimerTask() {

				@Override
				public void run() {
					db.flush();
				}
			}, 0, 60000);
		}
		return db;
	}

	@Override
	public void cleanUp() {
		if (db != null)
			db.flush();
		executor.cancel();
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
