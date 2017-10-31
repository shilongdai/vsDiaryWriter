package net.viperfish.journal2.error;

public final class CompromisedDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7656502099149561709L;

	private long id;

	public CompromisedDataException(long id) {
		this.id = id;
	}

	public CompromisedDataException(String s, long id) {
		super(s);
		this.id = id;
	}

	public CompromisedDataException(Throwable cause, long id) {
		super(cause);
		this.id = id;
	}

	public CompromisedDataException(String message, Throwable cause, long id) {
		super(message, cause);
		this.id = id;
	}

	public long getID() {
		return id;
	}

}
