package net.viperfish.journal.ui;

import java.util.List;
import java.util.Set;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.operation.AddEntryOperation;
import net.viperfish.journal.operation.ChangeAuthManagerOperation;
import net.viperfish.journal.operation.ChangeDataSourceOperation;
import net.viperfish.journal.operation.ChangeIndexerOperation;
import net.viperfish.journal.operation.ChangeJournalTransformerOperation;
import net.viperfish.journal.operation.DeleteEntryOperation;
import net.viperfish.journal.operation.EditContentOperation;
import net.viperfish.journal.operation.EditSubjectOperation;
import net.viperfish.journal.operation.GetAllOperation;
import net.viperfish.journal.operation.GetEntryOperation;
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
	public Operation getChangeDBOperation(String newDB) {
		return new ChangeDataSourceOperation(newDB);
	}

	@Override
	public Operation getChangeIndexerOperation(String newIndexer) {
		return new ChangeIndexerOperation(newIndexer);
	}

	@Override
	public Operation getChangeTransformerOperation(String newTrans) {
		return new ChangeJournalTransformerOperation(newTrans);
	}

	@Override
	public Operation getChangeAuthManagerOperation(String newAuth) {
		return new ChangeAuthManagerOperation(newAuth);
	}

}
