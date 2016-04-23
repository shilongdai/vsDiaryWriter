package net.viperfish.journal.framework.errors;

public class OperationErrorException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7639756645902530904L;

	public OperationErrorException() {
		// TODO Auto-generated constructor stub
	}

	public OperationErrorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public OperationErrorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public OperationErrorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public OperationErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
