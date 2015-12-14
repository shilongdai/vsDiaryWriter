package net.viperfish.journal.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.viperfish.journal.framework.Journal;

public class JournalCellRenderer implements ListCellRenderer<Journal> {

	public HashMap<Journal, JournalCellJPanel> journalCells = new HashMap<Journal, JournalCellJPanel>();

	public JList<? extends Journal> list;

	public Component getListCellRendererComponent(JList<? extends Journal> list, Journal value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JournalCellJPanel journalCell = new JournalCellJPanel(value, false, isSelected);
		journalCells.put(value, journalCell);
		this.list = list;
		return journalCell;
	}

	/**
	 * not used
	 * @param e
	 */
	public void updateMouseSelection(MouseEvent e) {
		float Y = e.getY();
		int i = 0;
		while (Y > 0 && i < list.getModel().getSize()) {
			Y -= list.getFixedCellHeight();
			System.out.println(list.getFixedCellHeight());
			i++;
		}
		if (Y > 0) {
			i = -1;
		}
		System.out.println(list.getSelectedIndex());
		if(i < 0){
			list.clearSelection();
		}
		list.setSelectedIndex(i);
	}

}
