package net.viperfish.journal.ui;

import java.util.List;
import java.util.Set;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationWithResult;

/**
 * the higher level api facade factory for creating an appropriate operation for
 * a task
 * 
 * @see Operation
 * @see OperationWithResult
 * @author sdai
 *
 */
public interface OperationFactory {
	/**
	 * get an operation that will add a Journal to the persistence
	 * 
	 * @param toAdd
	 *            the journal to add
	 * @return an add operation
	 */
	public Operation getAddOperation(Journal toAdd);

	/**
	 * get an operation that will remove a Journal from the persistence
	 * 
	 * @param id
	 *            the id of the journal to remove
	 * @return the operation
	 */
	public Operation getDeleteOperation(Long id);

	/**
	 * get an operation that will edit the content of a Journal
	 * 
	 * @param id
	 *            the id of the Journal
	 * @param content
	 *            the new content
	 * @return the operation
	 */
	public Operation getEditContentOperation(Long id, String content);

	/**
	 * get an operation that will edit the subject of a Journal
	 * 
	 * @param id
	 *            the id of the journal
	 * @param sub
	 *            the edited subject
	 * @return the operation
	 */
	public Operation getEditSubjectOperation(Long id, String sub);

	/**
	 * get an operation that has a result, which is a list of all Journal Entry,
	 * from the persistence
	 * 
	 * @return the operation with result
	 */
	public OperationWithResult<List<Journal>> getListAllOperation();

	/**
	 * get an operation that has a result, a list of Journal that matches a
	 * search
	 * 
	 * @param query
	 *            the search query
	 * @return operation with results
	 */
	public OperationWithResult<Set<Journal>> getSearchOperation(String query);

	/**
	 * get an operation that results in getting a Journal
	 * 
	 * @param id
	 *            the id of the journal to get
	 * @return the operation with result
	 */
	public OperationWithResult<Journal> getGetEntryOperation(Long id);

}
