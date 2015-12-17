package net.viperfish.journal.gui;

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

public class PasswordPrompt extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2459610926470946507L;
	private JPanel contentPane;
	private JPasswordField passwordField;
	private JButton btnLogin;

	/**
	 * Create the frame.
	 * 
	 * @param graphicalUserInterface
	 */
	public PasswordPrompt(final GraphicalUserInterface graphicalUserInterface) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 418, 208);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow 20][grow 80][]", "[][grow 20][][][grow 20][]"));

		JLabel lblWelcomeBack = new JLabel("Welcome Back!");
		lblWelcomeBack.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblWelcomeBack, "cell 0 0 3 1");

		JLabel lblCreateAPassword = new JLabel("Enter Your Password");
		lblCreateAPassword.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblCreateAPassword, "cell 1 2,alignx left,growy");

		passwordField = new JPasswordField();
		contentPane.add(passwordField, "cell 1 3,grow");

		passwordField.getDocument().addDocumentListener(new DocumentListener() {
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
				btnLogin.setEnabled(passwordField.getPassword().length > 0);
			}
		});

		btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = getPassword();
				if (graphicalUserInterface.auth(PasswordPrompt.this)) {
					dispose();
				}
			}
		});
		btnLogin.setEnabled(false);

		contentPane.add(btnLogin, "cell 2 5,alignx right,aligny bottom");
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

}
