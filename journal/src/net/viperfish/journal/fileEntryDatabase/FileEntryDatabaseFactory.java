package net.viperfish.journal.fileEntryDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.utils.file.IOFile;

public abstract class FileEntryDatabaseFactory implements DataSourceFactory {

	private FileEntryDatabase db;
	private File dataFile;
	private final Timer executor;

	public FileEntryDatabaseFactory() {
		executor = new Timer("flusher");
	}

	protected abstract IOFile createIOFile(File dataFile);

	@Override
	public EntryDatabase createDatabaseObject() {
		db = new FileEntryDatabase(createIOFile(dataFile));
		db.load();
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
