package net.viperfish.journal.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;

public class MainWindow extends JFrame {

	private JPanel contentPane;
	private JTextField searchField;
	private OperationFactory ops;
	private OperationExecutor e;
	private JList<Journal> entryList;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		ops = JournalApplication.getOperationFactory();
		e = JournalApplication.getWorker();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 541);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("inset 0, fill", "[grow][][grow][]", "[][][][grow]"));

		JLabel lblJournalTitle = new JLabel("Journal 2");
		lblJournalTitle.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblJournalTitle, "cell 0 0");

		JButton btnNewEntry = new JButton("New Entry");
		contentPane.add(btnNewEntry, "cell 3 1");

		JLabel lblYourEntries = new JLabel("Your Journals");
		lblYourEntries.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblYourEntries, "cell 0 2,alignx left");

		JLabel lblSearch = new JLabel("Search");
		lblSearch.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblSearch, "cell 1 2,alignx trailing");

		searchField = new JTextField();

		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			public void insertUpdate(DocumentEvent e) {
				update();
			}

			public void changedUpdate(DocumentEvent e) {
				update();
			}

			public void update() {
				updateEntries();
			}
		});
		contentPane.add(searchField, "cell 2 2 2 1,growx");
		searchField.setColumns(10);

		entryList = new JList<Journal>();
		contentPane.add(entryList, "cell 0 3 4 1,grow");
		entryList.setModel(new JournalListModel(new ArrayList<Journal>()));
		entryList.setCellRenderer(new JournalCellRenderer(this));
		updateEntries();
	}

	public void updateEntries() {
		List<Journal> journalList = null;
		String query = searchField.getText();
		if (query.length() > 0) {
			OperationWithResult<Set<Journal>> ops = this.ops.getSearchOperation(query);
			e.submit(ops);
			Set<Journal> journals = ops.getResult();
			journalList = new ArrayList<Journal>(journals);
		} else {
			OperationWithResult<List<Journal>> ops = this.ops.getListAllOperation();
			e.submit(ops);
			journalList = ops.getResult();
		}
		JournalListModel model = (JournalListModel) entryList.getModel();
		model.setJournals(journalList);
		System.out.println("done");
		
	}

	public void editJournal(Journal journal) {
		// TODO Edit
	}

}
