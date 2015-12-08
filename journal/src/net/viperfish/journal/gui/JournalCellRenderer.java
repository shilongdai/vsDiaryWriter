package net.viperfish.journal.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import net.viperfish.journal.framework.Journal;

public class JournalCellRenderer implements ListCellRenderer<Journal> {
	
	public Component getListCellRendererComponent(JList<? extends Journal> list, Journal value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JournalCellJPanel journalCell = new JournalCellJPanel(value, false,isSelected);
		return journalCell;
	}

}
