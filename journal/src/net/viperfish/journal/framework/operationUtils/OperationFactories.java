package net.viperfish.journal.framework.operationUtils;

public class OperationFactories {

	static {
		ops = new StandardOperationFactory();
	}

	private static StandardOperationFactory ops;

	private OperationFactories() {

	}

	public static OperationFactory getOperationFactory() {
		return ops;
	}

	public static OperationFactory newOperationFactory() {
		return new StandardOperationFactory();
	}
}
