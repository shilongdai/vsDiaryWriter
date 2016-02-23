package net.viperfish.journal.operation;

import java.io.File;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.json.JsonGenerator;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;
import net.viperfish.utils.index.Indexer;

public class ImportEntriesOperation implements Operation {

	private IOFile importFile;
	private static final JsonGenerator generator;
	private EntryDatabase db;
	private Indexer<Journal> indexer;

	static {
		generator = new JsonGenerator();
	}

	public ImportEntriesOperation(String importFile) {
		this.importFile = new IOFile(new File(importFile), new TextIOStreamHandler());
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = Indexers.INSTANCE.getIndexer();
	}

	@Override
	public void execute() {
		String json = importFile.read(StandardCharsets.UTF_16);
		try {
			Journal[] result = generator.fromJson(Journal[].class, json);
			for (Journal i : result) {
				db.addEntry(i);
				indexer.add(i);
			}
		} catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}

	}

}
