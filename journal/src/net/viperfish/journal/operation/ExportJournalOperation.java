package net.viperfish.journal.operation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import net.viperfish.journal.framework.InjectedOperation;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.errors.FailToExportEntriesException;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;
import net.viperfish.utils.serialization.JsonGenerator;

/**
 * exports all entries to a text file. Entries are serialized in JSON format
 * 
 * @author sdai
 *
 */
final class ExportJournalOperation extends InjectedOperation {

	static {
		generator = new JsonGenerator();
	}

	private static final JsonGenerator generator;
	private IOFile outputTarget;

	ExportJournalOperation(String outputFile) {
		outputTarget = new IOFile(new File(outputFile), new TextIOStreamHandler());
	}

	@Override
	public void execute() {
		// get all entries
		List<Journal> allJournals = db().getAll();
		Journal[] toExport = allJournals.toArray(new Journal[0]);

		// set the id to null to reassign id when importing
		for (Journal i : toExport) {
			i.setId(null);
		}

		// serialize to JSON and write
		try {
			String result = generator.toJson(toExport);
			outputTarget.write(result, StandardCharsets.UTF_16);
		} catch (IOException e) {
			FailToExportEntriesException f = new FailToExportEntriesException(
					"Cannot export entries to:" + outputTarget + " message:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(f);
		}
	}

}
