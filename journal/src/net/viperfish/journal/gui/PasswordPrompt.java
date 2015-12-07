package net.viperfish.journal.gui;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PasswordPrompt extends JFrame {

	private JPanel contentPane;
	private JPasswordField passwordField;

	/**
	 * Create the frame.
	 * @param graphicalUserInterface 
	 */
	public PasswordPrompt(GraphicalUserInterface graphicalUserInterface) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 406, 179);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow][grow]", "[][][][][]"));

		JLabel lblWelcomeBack = new JLabel("Welcome Back!");
		lblWelcomeBack.setFont(new Font("Consolas", Font.PLAIN, 30));
		contentPane.add(lblWelcomeBack, "cell 0 0 2 1");

		JLabel lblCreateAPassword = new JLabel("Enter Your Password");
		contentPane.add(lblCreateAPassword, "cell 0 1,alignx left");

		passwordField = new JPasswordField();
		contentPane.add(passwordField, "cell 0 2 2 1,growx");
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = getPassword();
				if(graphicalUserInterface.auth(PasswordPrompt.this)){
					dispose();
				}
			}
		});
		btnLogin.setEnabled(false);
		
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
				btnLogin.setEnabled(passwordField.getPassword().length > 0);
			}
		});

		contentPane.add(btnLogin, "cell 1 4,alignx right");
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

}
