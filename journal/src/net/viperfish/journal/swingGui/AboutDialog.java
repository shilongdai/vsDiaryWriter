package net.viperfish.journal.swingGui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8312727757928274069L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 */
	public AboutDialog(JFrame jframe) {
		super(jframe);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(jframe);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[][][]"));
		{
			JLabel lblJournal = new JLabel("Journal 2");
			lblJournal.setFont(GraphicalUserInterface.defaultDialogTitleFont);
			contentPanel.add(lblJournal, "cell 0 0,alignx center");
		}
		{
			JLabel lblCreatorShilongDai = new JLabel(
					"Creator and Founder: Shilong Dai");
			lblCreatorShilongDai
					.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			contentPanel.add(lblCreatorShilongDai, "cell 0 1,alignx center");
		}
		{
			JLabel lblContributorAndGui = new JLabel(
					"Contributor and Gui: Ryan Segerstrom");
			lblContributorAndGui
					.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			contentPanel.add(lblContributorAndGui, "cell 0 2,alignx center");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton closeButton = new JButton("Close");
				closeButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				closeButton.setActionCommand("Cancel");
				buttonPane.add(closeButton);
			}
		}
	}

}
