package net.viperfish.journal.framework.operationUtils;

import net.viperfish.journal.framework.OperationExecutor;

public class OperationExecutors {
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
