package net.viperfish.journal.operation;

import java.io.File;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;
import net.viperfish.utils.serialization.JsonGenerator;

public class ImportEntriesOperation extends InjectedOperation {

	private IOFile importFile;
	private static final JsonGenerator generator;

	static {
		generator = new JsonGenerator();
	}

	public ImportEntriesOperation(String importFile) {
		this.importFile = new IOFile(new File(importFile), new TextIOStreamHandler());
	}

	@Override
	public void execute() {
		String json = importFile.read(StandardCharsets.UTF_16);
		try {
			Journal[] result = generator.fromJson(Journal[].class, json);
			for (Journal i : result) {
				db().addEntry(i);
				indexer().add(i);
			}
		} catch (JsonParseException | JsonMappingException e) {
			throw new RuntimeException(e);
		}

	}

}
