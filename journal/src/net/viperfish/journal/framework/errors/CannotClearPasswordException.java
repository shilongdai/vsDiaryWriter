package net.viperfish.journal.framework.errors;

import java.io.IOException;

public class CannotClearPasswordException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2578119530783646892L;

	public CannotClearPasswordException() {
		// TODO Auto-generated constructor stub
	}

	public CannotClearPasswordException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CannotClearPasswordException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CannotClearPasswordException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
