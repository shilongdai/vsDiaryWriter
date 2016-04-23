package net.viperfish.journal.operation;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalPointer;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * operation factory using built in operations
 * 
 * @author sdai
 *
 */
final class StandardOperationFactory implements OperationFactory {

	StandardOperationFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Operation getAddOperation(Journal toAdd) {
		if (toAdd == null) {
			throw new NullArgumentException("toAdd");
		}
		return new AddEntryOperation(toAdd);
	}

	@Override
	public Operation getDeleteOperation(Long id) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		return new DeleteEntryOperation(id);
	}

	@Override
	public Operation getEditContentOperation(Long id, String content) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		return new EditContentOperation(id, content);
	}

	@Override
	public Operation getEditSubjectOperation(Long id, String sub) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		return new EditSubjectOperation(id, sub);
	}

	@Override
	public OperationWithResult<? extends Collection<JournalPointer>> getListAllOperation() {
		return new GetAllOperation();
	}

	@Override
	public OperationWithResult<? extends Collection<JournalPointer>> getSearchOperation(String query) {
		if (query == null) {
			query = "";
		}
		return new SearchEntryOperation(query);
	}

	@Override
	public OperationWithResult<Journal> getGetEntryOperation(Long id) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		return new GetEntryOperation(id);
	}

	@Override
	public Operation getClearEntriesOperation() {
		return new ClearEntriesOperation();
	}

	@Override
	public Operation getExportEntriesOperation(String targetFile) {
		if (targetFile == null) {
			throw new NullArgumentException("targetFile");
		}
		if (targetFile.length() == 0) {
			throw new IllegalArgumentException("Target file must be a valid file name");
		}
		return new ExportJournalOperation(targetFile);
	}

	@Override
	public Operation getImportEntriesOperation(String srcFile) {
		if (srcFile == null) {
			throw new NullArgumentException("srcFile");
		}
		if (srcFile.length() == 0) {
			throw new IllegalArgumentException("Source file must be a valid file name");
		}
		return new ImportEntriesOperation(srcFile);
	}

	@Override
	public Operation getChangePasswordOperation(String newPass) {
		if (newPass == null) {
			throw new NullArgumentException("newPass");
		}
		return new ChangePasswordOperation(newPass);
	}

	@Override
	public Operation getSetPasswordOperation(String pass) {
		if (pass == null) {
			throw new NullArgumentException("pass");
		}
		return new SetPasswordOperation(pass);
	}

	@Override
	public Operation getSetConfigOperation(Map<String, String> config) {
		if (config == null) {
			throw new NullArgumentException("config");
		}
		return new SetConfigurationOperation(config);
	}

	@Override
	public Operation getChangeConfigOperaion(Map<String, String> config) {
		if (config == null) {
			throw new NullArgumentException("config");
		}
		return new ChangeConfigurationOperation(config);
	}

	@Override
	public OperationWithResult<? extends Collection<JournalPointer>> getDateRangeOperation(Date lowerBound,
			Date upperBound) {
		if (lowerBound == null || upperBound == null) {
			throw new NullArgumentException("lowerBound and upperBound");
		}
		return new GetDateRangeOperation(lowerBound, upperBound);
	}

	@Override
	public OperationWithResult<? extends Collection<JournalPointer>> getDateRangeSearchOperation(String keyword,
			Date lower, Date upper) {
		if (keyword == null) {
			keyword = "";
		}
		if (lower == null || upper == null) {
			throw new NullArgumentException("lower and upper");
		}
		return new SearchOperationDateFilter(new SearchEntryOperation(keyword), upper, lower);
	}

}
