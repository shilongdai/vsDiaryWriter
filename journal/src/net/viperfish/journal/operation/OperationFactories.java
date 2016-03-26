package net.viperfish.journal.operation;

import net.viperfish.journal.framework.OperationFactory;

/**
 * static for getting operation factories
 * 
 * @author sdai
 *
 */
final public class OperationFactories {

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
