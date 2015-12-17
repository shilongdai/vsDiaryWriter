package net.viperfish.journal.gui.setup;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.gui.GraphicalUserInterface;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SimpleOrExpert extends ConfigView {

	public JRadioButton rdbtnSimple;
	public JRadioButton rdbtnExpert;
	
	/**
	 * Create the panel.
	 */
	public SimpleOrExpert(final FirstTimeSetup firstTimeSetup) {
		super(firstTimeSetup);
		setLayout(new MigLayout("", "[50px:n][grow]", "[][grow]"));

		JLabel lblPickYourSetup = new JLabel("Pick Your Setup Method");
		lblPickYourSetup.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		add(lblPickYourSetup, "cell 0 0 2 1");

		JPanel panel = new JPanel();
		panel.setBorder(null);
		add(panel, "cell 1 1,grow");
		panel.setLayout(new MigLayout("", "[55px][][][][][]", "[23px][]"));

		rdbtnSimple = new JRadioButton("Simple");
		rdbtnSimple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnExpert.setSelected(!rdbtnSimple.isSelected());
				firstTimeSetup.cont();
			}
		});
		rdbtnSimple.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		panel.add(rdbtnSimple, "cell 0 0,alignx left,aligny top");

		rdbtnExpert = new JRadioButton("Expert");
		rdbtnExpert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnSimple.setSelected(!rdbtnExpert.isSelected());
				firstTimeSetup.cont();
			}
		});
		rdbtnExpert.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		panel.add(rdbtnExpert, "cell 0 1");

	}

}
