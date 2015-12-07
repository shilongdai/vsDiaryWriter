package net.viperfish.journal.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;

public class PasswordPrompt extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public PasswordPrompt() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 406, 179);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
		
		JLabel lblWelcomeBack = new JLabel("Welcome Back!");
		lblWelcomeBack.setFont(new Font("Consolas", Font.PLAIN, 30));
		contentPane.add(lblWelcomeBack, "cell 0 0 2 1");
		
		JLabel lblCreateAPassword = new JLabel("Enter Your Password");
		contentPane.add(lblCreateAPassword, "cell 0 1,alignx left");
		
		textField = new JTextField();
		contentPane.add(textField, "cell 0 2 2 1,growx");
		textField.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		contentPane.add(btnLogin, "cell 1 4,alignx right");
	}

}
