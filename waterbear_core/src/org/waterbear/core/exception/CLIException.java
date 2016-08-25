package org.waterbear.core.exception;

public class CLIException extends AutomationException {
	private String cliMessage = "";

	public CLIException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CLIException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CLIException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CLIException(String message, String cliMessage) {
		super(message + "CLI error:" + cliMessage);
		this.cliMessage = cliMessage;
	}

	public CLIException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public String getCliMessage() {
		return cliMessage;
	}

}
