package net.viperfish.journal.ui;

import java.util.List;
import java.util.Set;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.operation.AddEntryOperation;
import net.viperfish.journal.operation.ClearEntriesOperation;
import net.viperfish.journal.operation.DeleteEntryOperation;
import net.viperfish.journal.operation.EditContentOperation;
import net.viperfish.journal.operation.EditSubjectOperation;
import net.viperfish.journal.operation.ExportJournalOperation;
import net.viperfish.journal.operation.GetAllOperation;
import net.viperfish.journal.operation.GetEntryOperation;
import net.viperfish.journal.operation.ImportEntriesOperation;
import net.viperfish.journal.operation.SearchEntryOperation;

public class StandardOperationFactory implements OperationFactory {

	@Override
	public Operation getAddOperation(Journal toAdd) {
		return new AddEntryOperation(toAdd);
	}

	@Override
	public Operation getDeleteOperation(Long id) {
		return new DeleteEntryOperation(id);
	}

	@Override
	public Operation getEditContentOperation(Long id, String content) {
		return new EditContentOperation(id, content);
	}

	@Override
	public Operation getEditSubjectOperation(Long id, String sub) {
		return new EditSubjectOperation(id, sub);
	}

	@Override
	public OperationWithResult<List<Journal>> getListAllOperation() {
		return new GetAllOperation();
	}

	@Override
	public OperationWithResult<Set<Journal>> getSearchOperation(String query) {
		return new SearchEntryOperation(query);
	}

	@Override
	public OperationWithResult<Journal> getGetEntryOperation(Long id) {
		return new GetEntryOperation(id);
	}

	@Override
	public Operation getClearEntriesOperation() {
		return new ClearEntriesOperation();
	}

	@Override
	public Operation getExportEntriesOperation(String targetFile) {
		return new ExportJournalOperation(targetFile);
	}

	@Override
	public Operation getImportEntriesOperation(String srcFile) {
		return new ImportEntriesOperation(srcFile);
	}

}
