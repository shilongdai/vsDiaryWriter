package net.viperfish.utils.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Configuration {

	private static Map<String, ComponentConfig> data;
	private static File configDir;

	static {
		data = new HashMap<String, ComponentConfig>();
		configDir = new File("config");
		if (!configDir.exists()) {
			configDir.mkdir();
		}
	}

	public static ComponentConfig get(String key) {
		return data.get(key);
	}

	public static ComponentConfig put(String key, ComponentConfig value) {
		value.fillInDefault();
		return data.put(key, value);
	}

	public static void persistAll() throws IOException {
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

	public static void loadAll() throws IOException {
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

	public static Iterable<ComponentConfig> allComponent() {
		Set<ComponentConfig> all = new HashSet<>();
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			all.add(i.getValue());
		}
		return all;
	}

	public static void defaultAll() {
		for (Entry<String, ComponentConfig> i : data.entrySet()) {
			i.getValue().fillInDefault();
		}
	}

}
