package net.viperfish.journal.operation;

import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Operation;

public class SetConfigurationOperation implements Operation {

	private Map<String, String> newConfig;

	public SetConfigurationOperation(Map<String, String> config) {
		this.newConfig = config;
	}

	@Override
	public void execute() {
		for (Entry<String, String> i : newConfig.entrySet()) {
			Configuration.setProperty(i.getKey(), i.getValue());
		}
	}

}
