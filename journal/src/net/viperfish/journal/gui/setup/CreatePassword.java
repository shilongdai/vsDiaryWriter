package net.viperfish.journal.gui.setup;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.gui.GraphicalUserInterface;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class CreatePassword extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;

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
		setBounds(100, 100, 385, 217);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
		
		JLabel lblCreatePassword = new JLabel("Create Password");
		lblCreatePassword.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		contentPane.add(lblCreatePassword, "cell 0 0 2 1");
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblPassword, "cell 0 2,alignx right");
		
		textField = new JTextField();
		contentPane.add(textField, "cell 1 2,growx");
		textField.setColumns(10);
		
		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		contentPane.add(lblConfirmPassword, "cell 0 4,alignx right");
		
		textField_1 = new JTextField();
		contentPane.add(textField_1, "cell 1 4,growx");
		textField_1.setColumns(10);
	}

}
