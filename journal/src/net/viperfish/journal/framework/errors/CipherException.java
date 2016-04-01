package net.viperfish.journal.framework.errors;

import java.security.GeneralSecurityException;

public class CipherException extends GeneralSecurityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -726691328577239205L;

	public CipherException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CipherException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CipherException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public CipherException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
