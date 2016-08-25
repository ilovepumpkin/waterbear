package org.waterbear.core.exception;

public class TaskFailException extends AutomationException {
	private String taskMessages;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5030523060016274756L;

	public TaskFailException(String message, String taskMessages) {
		super(message + taskMessages);
		this.taskMessages = taskMessages;
	}

	public String getTaskMessages() {
		return taskMessages;
	}

}
