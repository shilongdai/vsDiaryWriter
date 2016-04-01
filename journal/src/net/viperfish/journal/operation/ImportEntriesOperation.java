package net.viperfish.journal.operation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToImportEntriesException;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;
import net.viperfish.utils.serialization.JsonGenerator;

/**
 * Operation to import entries from a text file containing enties in JSON
 * 
 * @author sdai
 *
 */
final class ImportEntriesOperation extends InjectedOperation {

	private IOFile importFile;
	private static final JsonGenerator generator;

	static {
		generator = new JsonGenerator();
	}

	ImportEntriesOperation(String importFile) {
		this.importFile = new IOFile(new File(importFile), new TextIOStreamHandler());
	}

	@Override
	public void execute() {
		// load the file
		try {
			String json = importFile.read(StandardCharsets.UTF_16);

			// de-serialize and add
			Journal[] result = generator.fromJson(Journal[].class, json);
			for (Journal i : result) {
				db().addEntry(i);
				indexer().add(i);
			}
		} catch (IOException e) {
			FailToImportEntriesException f = new FailToImportEntriesException(
					"Cannot import from:" + importFile.getFile() + " message:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(f);
		}

	}

}
