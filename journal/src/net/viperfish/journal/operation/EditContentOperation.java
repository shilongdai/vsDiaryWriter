package net.viperfish.journal.operation;

import net.viperfish.journal.framework.Journal;

public class EditContentOperation extends EditEntryOperation {

	private String content;

	public EditContentOperation(Long id, String c) {
		super(id);
		content = c;
	}

	@Override
	protected void edit(Journal e) {
		e.setContent(content);
	}

}
