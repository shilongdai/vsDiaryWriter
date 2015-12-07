package net.viperfish.journal.gui.setup;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JRadioButton;

public class SimpleOrExpert extends ConfigView {

	/**
	 * Create the panel.
	 */
	public SimpleOrExpert() {
		setLayout(new MigLayout("", "[50px:n][grow]", "[][grow]"));
		
		JLabel lblPickYourSetup = new JLabel("Pick Your Setup Method");
		lblPickYourSetup.setFont(new Font("Times New Roman", Font.PLAIN, 24));
		add(lblPickYourSetup, "cell 0 0 2 1");
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		add(panel, "cell 1 1,grow");
		panel.setLayout(new MigLayout("", "[55px]", "[23px][]"));
		
		JRadioButton rdbtnSimple = new JRadioButton("Simple");
		panel.add(rdbtnSimple, "cell 0 0,alignx left,aligny top");
		
		JRadioButton rdbtnExpert = new JRadioButton("Expert");
		panel.add(rdbtnExpert, "cell 0 1");

	}

}
