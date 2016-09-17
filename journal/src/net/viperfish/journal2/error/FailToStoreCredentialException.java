package net.viperfish.journal2.error;

import java.io.IOException;

public class FailToStoreCredentialException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4232480634152412915L;

	public FailToStoreCredentialException() {
		// TODO Auto-generated constructor stub
	}

	public FailToStoreCredentialException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FailToStoreCredentialException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public FailToStoreCredentialException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
