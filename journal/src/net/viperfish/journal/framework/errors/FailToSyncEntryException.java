package net.viperfish.journal.framework.errors;

import java.io.IOException;

public class FailToSyncEntryException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5456591387144568894L;

	public FailToSyncEntryException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FailToSyncEntryException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FailToSyncEntryException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FailToSyncEntryException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
