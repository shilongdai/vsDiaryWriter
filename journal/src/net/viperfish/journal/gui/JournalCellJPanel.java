package net.viperfish.journal.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.framework.Journal;

public class JournalCellJPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public JournalCellJPanel(MainWindow window, Journal journal, boolean mouseOver, boolean selected) {
		// setBackground(UIManager.getColor("List.background"));
		if (selected) {
			setBackground(UIManager.getColor("List.selectionForeground"));
		}

		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);

		JMenuItem mntmEdit = new JMenuItem("Edit");
		mntmEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				window.editJournal(journal);
			}
		});
		popupMenu.add(mntmEdit);

		JMenuItem mntmDelete = new JMenuItem("Delete");
		popupMenu.add(mntmDelete);
		setLayout(new MigLayout("", "[50px][grow]", "[22px]"));

		JLabel lblTitle = new JLabel(journal.getSubject());
		lblTitle.setFont(GraphicalUserInterface.defaultDialogTitleFont.deriveFont(((float) 16)));
		add(lblTitle, "cell 0 0,alignx left,aligny center");

		JLabel lblDate = new JLabel(journal.getDate().toString());
		add(lblDate, "cell 1 0,alignx right,aligny center");

		// TODO
		if (mouseOver) {
			JLabel lblMore = new JLabel("--- more ---");
			add(lblMore, "cell 0 1");
		}

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
