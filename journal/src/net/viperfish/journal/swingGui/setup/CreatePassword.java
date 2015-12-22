package net.viperfish.journal.swingGui.setup;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.swingGui.GraphicalUserInterface;

public class CreatePassword extends JFrame {

	private JPanel contentPane;
	public JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JButton btnNewButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CreatePassword frame = new CreatePassword();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CreatePassword() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 385, 247);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][][][][]"));

		JLabel lblCreatePassword = new JLabel("Create Password");
		lblCreatePassword.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblCreatePassword, "cell 0 0 2 1");

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblPassword, "cell 0 2,alignx trailing");

		passwordField = new JPasswordField();
		contentPane.add(passwordField, "cell 1 2,growx");
		passwordField.getDocument().addDocumentListener(new DocumentListener() {
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
				updateFields();
			}
		});

		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblConfirmPassword, "cell 0 4,alignx trailing");

		passwordField_1 = new JPasswordField();
		contentPane.add(passwordField_1, "cell 1 4,growx");
		passwordField_1.getDocument().addDocumentListener(new DocumentListener() {
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
				updateFields();
			}
		});
		btnNewButton = new JButton("Create Password");
		btnNewButton.setEnabled(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		contentPane.add(btnNewButton, "cell 1 6,alignx right");
		pack();
	}

	public void updateFields() {
		boolean valid = true;
		char[] password = passwordField.getPassword();
		char[] confirmpassword = passwordField_1.getPassword();
		if (password.length == 0 || confirmpassword.length == 0) {
			valid = false;
		}
		if (password.length != confirmpassword.length) {
			valid = false;
		} else {
			for (int i = 0; i < password.length; i++) {
				char p = password[i];
				char cp = confirmpassword[i];
				if (p != cp) {
					valid = false;
				}
			}
		}
		btnNewButton.setEnabled(valid);
	}

}
