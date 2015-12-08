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

	private static final long serialVersionUID = 338305999768033527L;

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

	private int nextConfigurationCellRow = 2;
	
	/**
	 * Add Configuration options to the JPanel
	 * @param configurationItems Configurations that will be added to the JPanel
	 */
	private void setup(Iterable<String> configurationItems) {
		for (String configurationItem : configurationItems) {
			JLabel lblConfiguraitonItem = new JLabel(configurationItem);
			// TODO REMOVE THIS SET FONT
			lblConfiguraitonItem.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			add(lblConfiguraitonItem, "cell 1 "+nextConfigurationCellRow+",alignx trailing");
			
			JComboBox<String> configuraitonItemOptionBox = new JComboBox<String>();
			configuraitonItemOptionBox.setFont(GraphicalUserInterface.defaultDialogOptionFont);
			configuraitonItemOptionBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					updateState();
					String keyFromValue = "";
					for(String iter : fields.keySet()){
						if(fields.get(iter).equals(configuraitonItemOptionBox)){
							keyFromValue = iter;
						}
					}
					componentConfig.setProperty(keyFromValue, (String)e.getItem());
				}
			});
			Set<String> optionList = componentConfig.getOptions(configurationItem);
			optionList.add("<choose one>");
			String[] options = optionList.toArray(new String[0]);
			configuraitonItemOptionBox.setModel(new DefaultComboBoxModel<String>(options));
			add(configuraitonItemOptionBox, "cell 2 "+nextConfigurationCellRow+",growx");
			fields.put(configurationItem, configuraitonItemOptionBox);
			nextConfigurationCellRow++;
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
