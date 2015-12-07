package net.viperfish.journal.gui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataListener;

import net.viperfish.journal.framework.Journal;

public class JournalListModel extends DefaultListModel<Journal> {

	private List<Journal> journals;

	public JournalListModel(List<Journal> journals) {
		this.journals = journals;
	}

	public int getSize() {
		return journals.size();
	}

	@Override
	public Journal getElementAt(int index) {
		return journals.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub

	}

	public void setJournals(List<Journal> journals) {
		this.journals = journals;
	}

}
