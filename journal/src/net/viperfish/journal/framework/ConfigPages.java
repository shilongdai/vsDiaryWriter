package net.viperfish.journal.framework;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ConfigPages {

	static {
		buffer = new LinkedList<>();
	}

	private static List<Class<? extends ConfigPage>> buffer;

	private ConfigPages() {
		// TODO Auto-generated constructor stub
	}

	public static void registerConfig(Class<? extends ConfigPage> page) {
		buffer.add(page);
	}

	public static void registerConfig(Collection<Class<? extends ConfigPage>> all) {
		for (Class<? extends ConfigPage> i : all) {
			buffer.add(i);
		}
	}

	public static List<Class<? extends ConfigPage>> getConfigs() {
		return buffer;
	}
}
