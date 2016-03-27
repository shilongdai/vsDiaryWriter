package net.viperfish.journal.framework;

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
