package net.viperfish.journal.operation;

import java.util.HashMap;
import java.util.Map;

import net.viperfish.journal.framework.Operation;

public final class StoreConfigurationBufferOperation implements Operation {

	private static Map<String, String> globalBuffer;

	public static Operation applyOperation() {
		Operation result = new ChangeConfigurationOperation(globalBuffer);
		globalBuffer.clear();
		return result;
	}

	static {
		globalBuffer = new HashMap<>();
	}

	private Map<String, String> conf;

	public StoreConfigurationBufferOperation(Map<String, String> conf) {
		this.conf = conf;
	}

	@Override
	public void execute() {
		globalBuffer.putAll(conf);

	}

}
