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

public abstract class ComponentConfig extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 406194704525149982L;
	private File configFile;
	private String unitName;
	private List<ComponentConfigObserver> observers;

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

	public void load() throws InvalidPropertiesFormatException, IOException {
		if (!getConfigFile().exists()) {
			return;
		}
		this.loadFromXML(new BufferedInputStream(new FileInputStream(
				getConfigFile())));
	}

	public void notifyObserver() {
		for (ComponentConfigObserver i : observers) {
			i.sendNotify(this);
		}
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public abstract Iterable<String> requiredConfig();

	public abstract Iterable<String> optionalConfig();

	public abstract void fillInDefault();

	public abstract Set<String> getOptions(String key);

}
