package net.viperfish.journal.operation;

import net.viperfish.journal.framework.Operation;
import net.viperfish.utils.config.ComponentConfig;
import net.viperfish.utils.config.Configuration;

public class ApplyConfigOperation implements Operation {

	private final String configUnit;

	public ApplyConfigOperation(String unit) {
		this.configUnit = unit;
	}

	@Override
	public void execute() {
		ComponentConfig c = Configuration.get(configUnit);
		c.notifyObserver();
	}

}
