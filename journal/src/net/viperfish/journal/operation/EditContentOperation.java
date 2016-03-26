package net.viperfish.journal.operation;

import net.viperfish.journal.framework.Journal;

/**
 * edits the content of an entry in the system
 * 
 * @author sdai
 *
 */
final class EditContentOperation extends EditEntryOperation {

	private String content;

	EditContentOperation(Long id, String c) {
		super(id);
		content = c;
	}

	@Override
	protected void edit(Journal e) {
		e.setContent(content);
	}

}
