package org.waterbear.core.exception;

public class InvalidDataException extends CLIException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5598102656820122279L;

	public InvalidDataException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidDataException(String message, String cliMessage) {
		super(message, cliMessage);
		// TODO Auto-generated constructor stub
	}

	public InvalidDataException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public InvalidDataException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidDataException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
