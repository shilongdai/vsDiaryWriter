package net.viperfish.journal.gui;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.viperfish.journal.framework.Journal;

public class JournalCellRenderer implements ListCellRenderer<Journal> {

	public HashMap<Integer, JournalCellJPanel> journalCells = new HashMap<Integer, JournalCellJPanel>();

	public Component getListCellRendererComponent(JList<? extends Journal> list, Journal value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JournalCellJPanel journalCell = new JournalCellJPanel(value, false, isSelected);
		journalCells.put(index, journalCell);
		return journalCell;
	}

	public void mouseMoved(MouseEvent e) {
		JournalCellJPanel jpanel = null;
		// TODO Make sneek peek
	}

}
