package net.viperfish.journal.gui.setup;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import net.viperfish.journal.gui.GraphicalUserInterface;
import net.viperfish.utils.config.ComponentConfig;

public class ComponentConfigSetup extends ConfigView {
	
	private HashMap<String, JComboBox<String>> fields = new HashMap<String, JComboBox<String>>();

	private ComponentConfig componentConfig;
	
	public ComponentConfigSetup(FirstTimeSetup firstTimeSetup, ComponentConfig componentConfig, boolean expert) {
		super(firstTimeSetup);
		this.componentConfig = componentConfig;
		setLayout(new MigLayout("", "[][][grow]", "[][][]"));

		JLabel lblNewLabel = new JLabel("Configuration For: "+componentConfig.getUnitName());
		lblNewLabel.setFont(GraphicalUserInterface.defaultDialogTitleFont);
		add(lblNewLabel, "cell 0 0 3 1");

		JLabel lblOptions = new JLabel("Options:");
		lblOptions.setFont(GraphicalUserInterface.defaultDialogOptionFont);
		add(lblOptions, "cell 0 1");

		Iterable<String> required = componentConfig.requiredConfig();
		setup(required);
		if (expert) {
			Iterable<String> optional = componentConfig.optionalConfig();
			setup(optional);
		}
		updateState();
	}

	int cellRow = 2;
	private void setup(Iterable<String> list) {
		for (String iter : list) {
			JLabel lblNewLabel_1 = new JLabel(iter);
			lblNewLabel_1.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			add(lblNewLabel_1, "cell 1 "+cellRow+",alignx trailing");
			Set<String> optionList = componentConfig.getOptions(iter);
			optionList.add("<choose one>");
			String[] options = optionList.toArray(new String[0]);
			JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			comboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateState();
					String keyFromValue = "";
					for(String iter : fields.keySet()){
						if(fields.get(iter).equals(comboBox)){
							keyFromValue = iter;
						}
					}
					componentConfig.setProperty(keyFromValue, (String)e.getItem());
				}
			});
			fields.put(iter, comboBox);
			comboBox.setModel(new DefaultComboBoxModel<String>(options));
			add(comboBox, "cell 2 "+cellRow+",growx");
			cellRow++;
		}
	}

	public void updateState(){
		boolean cont = true;
		for(String iter : fields.keySet()){
			String value = (String) fields.get(iter).getSelectedItem();
			if(value.equalsIgnoreCase("<choose one>")){
				cont = false;
			}
		}
		firstTimeSetup.cont(cont);
		if(fields.keySet().size() == 0){
			//
		}
	}
	
}
