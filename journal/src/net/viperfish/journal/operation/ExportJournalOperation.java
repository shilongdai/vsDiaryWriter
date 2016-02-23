package net.viperfish.journal.operation;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.json.JsonGenerator;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

public class ExportJournalOperation implements Operation {

	static {
		generator = new JsonGenerator();
	}

	private static final JsonGenerator generator;
	private IOFile outputTarget;
	private EntryDatabase db;

	public ExportJournalOperation(String outputFile) {
		outputTarget = new IOFile(new File(outputFile), new TextIOStreamHandler());
		db = EntryDatabases.INSTANCE.getEntryDatabase();
	}

	@Override
	public void execute() {
		List<Journal> allJournals = db.getAll();
		Journal[] toExport = allJournals.toArray(new Journal[1]);
		try {
			String result = generator.toJson(toExport);
			outputTarget.write(result, StandardCharsets.UTF_16);
		} catch (JsonGenerationException | JsonMappingException e) {
			throw new RuntimeException(e);
		}
	}

}
