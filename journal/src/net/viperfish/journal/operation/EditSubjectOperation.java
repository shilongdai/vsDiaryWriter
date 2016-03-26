package net.viperfish.journal.operation;

import net.viperfish.journal.framework.Journal;

/**
 * edit the subject of an entry
 * 
 * @author sdai
 *
 */
final class EditSubjectOperation extends EditEntryOperation {

	private String subject;

	EditSubjectOperation(Long id, String s) {
		super(id);
		this.subject = s;
	}

	@Override
	protected void edit(Journal e) {
		e.setSubject(subject);

	}

}
