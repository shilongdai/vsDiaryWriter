package net.viperfish.utils.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.viperfish.utils.file.RecursiveDelete;

/**
 * the configuration utility
 * 
 * @author sdai
 * 
 */
public class Configuration {

	private static Map<String, ComponentConfig> data;
	private static File configDir;
	private static String configDirPath;

	private static File getConfigDir() {
		if (configDir == null) {
			configDir = new File(configDirPath);
			if (!configDir.exists()) {
				configDir.mkdir();
			}
		}
		return configDir;
	}

	static {
		data = new HashMap<String, ComponentConfig>();
	}

	public static String getConfigDirPath() {
		return configDirPath;
	}

	public static void setConfigDirPath(String configDirPath) {
		Configuration.configDirPath = configDirPath;
	}

	
	public static void clear() {
		new RecursiveDelete().recursiveRm(getConfigDir());
		data.clear();
	}
	/**
	 * get a configuration unit with a id of key
	 * 
	 * @param key
	 *            the identifier of the unit
	 * @return the configuration unit
	 */
	public static ComponentConfig get(String key) {
		return data.get(key);
	}

	/**
	 * add a config unit to the this manager
	 * 
	 * @param key
	 *            the identifier of the configurationUnit
	 * @param value
	 *            the unit
	 * @return the added unit
	 */
	public static ComponentConfig put(String key, ComponentConfig value) {
		value.fillInDefault();
		return data.put(key, value);
	}

	/**
	 * store all configuration to xml files located in configuration directory,
	 * currently ./config
	 * 
	 * @throws IOException
	 *             if any thing went wrong when storing
	 */
	public static void persistAll() throws IOException {
		File configDir = getConfigDir();
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			i.getValue().setProperty("ConfigFileLocation",
					configDir.getCanonicalPath() + "/" + i.getKey());
			try {
				i.getValue().persist();
			} catch (IOException e) {
				e.fillInStackTrace();
				throw e;
			}
		}
	}

	/**
	 * load all added configuration from their config files with the
	 * corresponding names located in the ./config directory
	 * 
	 * @throws IOException
	 *             if error on reading, and parsing
	 */
	public static void loadAll() throws IOException {
		File configDir = getConfigDir();
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			i.getValue().setProperty("ConfigFileLocation",
					configDir.getCanonicalPath() + "/" + i.getKey());
			try {
				i.getValue().load();
			} catch (IOException e) {
				throw e;
			}
		}
	}

	/**
	 * get all added component
	 * 
	 * @return all component
	 */
	public static Iterable<ComponentConfig> allComponent() {
		Set<ComponentConfig> all = new HashSet<>();
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			all.add(i.getValue());
		}
		return all;
	}

	/**
	 * call the fillInDefault method on all ComponentConfig units
	 * 
	 * @see ComponentConfig#fillInDefault()
	 */
	public static void defaultAll() {
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			i.getValue().fillInDefault();
		}
	}

}
