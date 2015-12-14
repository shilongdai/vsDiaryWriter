package net.viperfish.journal.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.gui.setup.textEditor.JournalEditor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JournalWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1308925768920327211L;
	private JPanel contentPane;
	private JTextField searchField;
	private OperationFactory of;
	private OperationExecutor e;
	private JList<Journal> entryList;
	private GraphicalUserInterface userInterface;

	/**
	 * Create the frame.
	 */
	public JournalWindow(GraphicalUserInterface userInterface) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				JournalApplication.cleanUp();
			}
		});
		this.userInterface = userInterface;
		of = JournalApplication.getOperationFactory();
		e = JournalApplication.getWorker();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 820, 541);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addJournal();
			}
		});
		mnFile.add(mntmNew);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("inset 0, fill", "[grow][][grow][]", "[][][][grow]"));

		JLabel lblJournalTitle = new JLabel("Journal 2");
		lblJournalTitle.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblJournalTitle, "cell 0 0");

		JLabel lblYourEntries = new JLabel("Your Journals");
		lblYourEntries.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblYourEntries, "cell 0 2,alignx left");

		JLabel lblSearch = new JLabel("Search");
		lblSearch.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblSearch, "cell 1 2,alignx trailing");

		searchField = new JTextField();

		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			public void update() {
				EventQueue.invokeLater(new Runnable() {

					@Override
					public void run() {
						updateEntries();

					}
				});
			}
		});
		contentPane.add(searchField, "cell 2 2 2 1,growx");
		searchField.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		contentPane.add(scrollPane, "cell 0 3 4 1,grow");

		entryList = new JList<Journal>();
		entryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(entryList);
		entryList.setModel(new JournalListModel(new ArrayList<Journal>()));
		entryList.setCellRenderer(new JournalCellRenderer());

		entryList.addMouseListener(new MouseAdapter() {

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
				JournalCellRenderer cellRenderer = (JournalCellRenderer) entryList.getCellRenderer();
				cellRenderer.updateMouseSelection(e);

				Journal journal = entryList.getSelectedValue();

				JPopupMenu popupMenu = new JPopupMenu();
				if (journal == null) {
					JMenuItem mntmAddJournal = new JMenuItem("Add Journal");
					popupMenu.add(mntmAddJournal);

					mntmAddJournal.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addJournal();
						}
					});
				} else {
					// Journal options
				}
				popupMenu.show(entryList, e.getX(), e.getY());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Journal journal = entryList.getSelectedValue();
					editJournal(journal);
				}
			}
		});

		updateEntries();
	}

	/**
	 * Update entryList based off new Search Parameter if they exist, otherwise
	 * show full list.
	 */
	public void updateEntries() {
		// Search Parameters
		String query = searchField.getText().trim();
		List<Journal> journalList = null;
		// If Search Parameters exist, use Search Operations otherwise
		// use
		// Normal List All Option.
		if (query.length() > 0) {
			System.out.println("Searching for '" + query + "'");
			OperationWithResult<Set<Journal>> ops = of.getSearchOperation(query);
			e.submit(ops);
			System.err.println("submitted");
			// Convert Set to List
			Set<Journal> journals = ops.getResult();
			journalList = new ArrayList<Journal>(journals);
		} else {
			System.out.println("Showing All ");
			OperationWithResult<List<Journal>> ops = of.getListAllOperation();
			e.submit(ops);
			System.err.println("submitted");
			journalList = ops.getResult();
		}
		System.out.println("done");
		// Update List Model
		JournalListModel model = (JournalListModel) entryList.getModel();
		model.setJournals(journalList);
		entryList.updateUI();
	}

	public void editJournal(Journal journal) {
		JournalEditor editor = new JournalEditor(this, journal);
		editor.setVisible(true);
	}

	public void addJournal() {
		Journal journal = new Journal();
		editJournal(journal);
	}

}
