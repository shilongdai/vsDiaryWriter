package net.viperfish.journal.operation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * operation factory using built in operations
 * 
 * @author sdai
 *
 */
class StandardOperationFactory implements OperationFactory {

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

	@Override
	public Operation getChangePasswordOperation(String newPass) {
		return new ChangePasswordOperation(newPass);
	}

	@Override
	public Operation getSetPasswordOperation(String pass) {
		return new SetPasswordOperation(pass);
	}

	@Override
	public Operation getSetConfigOperation(Map<String, String> config) {
		return new SetConfigurationOperation(config);
	}

	@Override
	public Operation getChangeConfigOperaion(Map<String, String> config) {
		return new ChangeConfigurationOperation(config);
	}

	@Override
	public OperationWithResult<Set<Journal>> getDateRangeOperation(Date lowerBound, Date upperBound) {
		return new GetDateRangeOperation(lowerBound, upperBound);
	}

	@Override
	public OperationWithResult<Set<Journal>> getDateRangeSearchOperation(String keyword, Date lower, Date upper) {
		return new SearchOperationDateFilter(new SearchEntryOperation(keyword), upper, lower);
	}

}
