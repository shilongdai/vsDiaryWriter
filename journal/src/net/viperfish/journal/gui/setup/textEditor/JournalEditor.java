package net.viperfish.journal.gui.setup.textEditor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.gui.GraphicalUserInterface;
import net.viperfish.journal.gui.JournalWindow;

public class JournalEditor extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3617532609648839403L;
	private final JPanel contentPane;
	private final JTextField subjectField;
	private final DateLabel lblDate;
	private final OperationFactory of;
	private final OperationExecutor e;
	private Journal currentJournal;
	private final JEditorPane editorPane;
	private final JournalWindow window;
	private final JLabel lblStatus;

	/**
	 * Create the frame.
	 */
	public JournalEditor(JournalWindow window, Journal journal) {
		this.window = window;
		of = JournalApplication.getOperationFactory();
		e = JournalApplication.getWorker();
		currentJournal = journal;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 832, 618);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("inset 0, fill",
				"[][grow][grow 50][][][]", "[][][][grow]"));

		JLabel lblJournalEditor = new JLabel("Journal Editor");
		lblJournalEditor.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblJournalEditor, "cell 0 0 2 2");

		lblDate = new DateLabel();
		lblDate.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblDate, "cell 2 0 4 1,alignx right");

		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveJournal();
			}
		});

		lblStatus = new JLabel("Opening");
		contentPane.add(lblStatus, "cell 3 1");
		contentPane.add(btnNewButton, "cell 4 1");

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		contentPane.add(btnCancel, "cell 5 1");

		JLabel lblSubject = new JLabel("Subject");
		lblSubject.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblSubject, "cell 0 2,alignx trailing");

		subjectField = new JTextField();
		subjectField.setText(journal.getSubject());
		contentPane.add(subjectField, "cell 1 2 5 1,growx");
		subjectField.setColumns(10);

		editorPane = new JEditorPane();
		editorPane.setText(journal.getContent());
		contentPane.add(editorPane, "cell 0 3 6 1,grow");
		lblStatus.setText("");
	}

	public void saveJournal() {
		boolean isNew = false;
		if (currentJournal.getId() == null) {
			currentJournal = new Journal();
			isNew = true;
		}
		currentJournal.setSubject(subjectField.getText());
		currentJournal.setContent(editorPane.getText());
		updateJournal(isNew);
	}

	public void updateJournal(final boolean isNew) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				lblStatus.setText("Saving");
				if (!isNew) {
					long id = currentJournal.getId();
					e.submit(of.getEditSubjectOperation(id,
							currentJournal.getSubject()));
					e.submit(of.getEditContentOperation(id,
							currentJournal.getContent()));
				} else {
					e.submit(of.getAddOperation(currentJournal));
				}
				window.updateEntries();
				lblStatus.setText("Saved");
			}
		});
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b) {
			lblDate.start();
		}
	}

}
