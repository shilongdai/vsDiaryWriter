package net.viperfish.journal.framework.operationUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
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

	/**
	 * get an operation that can clear all entries
	 * 
	 * @return the operation that clear entries
	 */
	public Operation getClearEntriesOperation();

	/**
	 * get an operation that export all entries
	 * 
	 * @param targetFile
	 *            the file name to export to
	 * @return the operation to export
	 */
	public Operation getExportEntriesOperation(String targetFile);

	/**
	 * get an operation that import entries from a specified file
	 * 
	 * @param srcFile
	 *            the name of the file to export
	 * @return the operation to import
	 */
	public Operation getImportEntriesOperation(String srcFile);

	/**
	 * get an operation that changes the password and associated keys
	 * 
	 * @param newPass
	 *            the new password
	 * @return the operation to change password
	 */
	public Operation getChangePasswordOperation(String newPass);

	/**
	 * get an operation that INITIALIZES the password, not changing the
	 * password.
	 * 
	 * @param pass
	 *            the password to set
	 * @return the operation to set password
	 */
	public Operation getSetPasswordOperation(String pass);

	/**
	 * get an operation that INITIALIZES configurations, not change the
	 * configuration
	 * 
	 * @param config
	 *            the configuration to init
	 * @return the operation to init configurations
	 */
	public Operation getSetConfigOperation(Map<String, String> config);

	/**
	 * get an operation that changes configuration
	 * 
	 * @param config
	 *            the new configuration
	 * @return the operation to change configuration
	 */
	public Operation getChangeConfigOperaion(Map<String, String> config);

	/**
	 * get an operation that gets a set of entries between the lower bound and
	 * the upper bound
	 * 
	 * @param lowerBound
	 *            the earliest possible date
	 * @param upperBound
	 *            the latest possible date
	 * @return the operation that get date range
	 */
	public OperationWithResult<Set<Journal>> getDateRangeOperation(Date lowerBound, Date upperBound);

}
