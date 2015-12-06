package net.viperfish.journal.framework;

import java.util.List;
import java.util.Set;

public interface OperationFactory {
	public Operation getAddOperation(Journal toAdd);

	public Operation getDeleteOperation(Long id);

	public Operation getEditContentOperation(Long id, String content);

	public Operation getEditSubjectOperation(Long id, String sub);

	public OperationWithResult<List<Journal>> getListAllOperation();

	public OperationWithResult<Set<Journal>> getSearchOperation(String query);

	public OperationWithResult<Journal> getGetEntryOperation(Long id);
}
