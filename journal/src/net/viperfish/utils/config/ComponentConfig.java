package net.viperfish.utils.config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * a unit of configuration
 * 
 * @author sdai
 *
 */
public abstract class ComponentConfig extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 406194704525149982L;
	private File configFile;
	private String unitName;
	private List<ComponentConfigObserver> observers;

	/**
	 * init the ComponentConfig
	 * 
	 * @param unitName
	 *            the name of this unit
	 */
	public ComponentConfig(String unitName) {
		super();
		this.unitName = unitName;
		observers = new LinkedList<ComponentConfigObserver>();
	}

	private File getConfigFile() {
		if (configFile == null) {
			configFile = new File(this.getProperty("ConfigFileLocation"));
		}
		return configFile;
	}

	/**
	 * store the configurations to a xml file
	 * 
	 * @throws IOException
	 */
	public void persist() throws IOException {
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		this.storeToXML(new BufferedOutputStream(new FileOutputStream(
				getConfigFile())), "config for " + unitName);
	}

	/**
	 * load the configuration file
	 * 
	 * @throws InvalidPropertiesFormatException
	 *             if invalid format
	 * @throws IOException
	 *             if failed to read
	 */
	public void load() throws InvalidPropertiesFormatException, IOException {
		if (!getConfigFile().exists()) {
			return;
		}
		this.loadFromXML(new BufferedInputStream(new FileInputStream(
				getConfigFile())));
	}

	/**
	 * notify all observer on this unit
	 */
	public void notifyObserver() {
		for (ComponentConfigObserver i : observers) {
			i.sendNotify(this);
		}
	}

	/**
	 * add a observer to this unit
	 * 
	 * @param o
	 *            the observer to add
	 */
	public void addObserver(ComponentConfigObserver o) {
		observers.add(o);
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * get key for required configuration for the user to fill out
	 * 
	 * @return
	 */
	public abstract Iterable<String> requiredConfig();

	/**
	 * get key for optional configuration for the user to fill out
	 * 
	 * @return
	 */
	public abstract Iterable<String> optionalConfig();

	/**
	 * init defaults
	 */
	public abstract void fillInDefault();

	/**
	 * get a set of available options for a key
	 * 
	 * @param key
	 *            the key to query for
	 * @return available options
	 */
	public abstract Set<String> getOptions(String key);

}
