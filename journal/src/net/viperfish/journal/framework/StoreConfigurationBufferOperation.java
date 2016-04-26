package net.viperfish.journal.framework;

import java.util.HashMap;
import java.util.Map;

import net.viperfish.journal.operation.OperationFactories;

public final class StoreConfigurationBufferOperation implements Operation {

	private static Map<String, String> globalBuffer;

	public static Operation applyOperation() {
		Operation result = OperationFactories.getOperationFactory().getChangeConfigOperaion(globalBuffer);
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
