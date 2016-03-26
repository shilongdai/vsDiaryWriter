package net.viperfish.journal.operation;

import net.viperfish.journal.framework.OperationExecutor;

/**
 * static for getting OperationExecutors
 * 
 * @author sdai
 *
 */
final public class OperationExecutors {
	private static ThreadPoolOperationExecutor exec;

	static {
		exec = new ThreadPoolOperationExecutor();
	}

	private OperationExecutors() {

	}

	public static OperationExecutor getExecutor() {
		return exec;
	}

	public static OperationExecutor newExecutor() {
		return new ThreadPoolOperationExecutor();
	}

	public static void dispose() {
		exec.terminate();
	}

}
