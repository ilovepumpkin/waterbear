package org.waterbear.core.exception;

import org.apache.log4j.Logger;

public class AutomationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7040462387209326541L;
	protected static Logger log = Logger.getLogger(AutomationException.class
			.getSimpleName());

	public AutomationException() {
		// TODO Auto-generated constructor stub
	}

	public AutomationException(String message) {
		super(message);
		log.error(message);
	}

	public AutomationException(Throwable cause) {
		super(cause);
		log.error(cause.getMessage());
	}

	public AutomationException(String message, Throwable cause) {
		super(message, cause);
		log.error(message);
		log.error(cause.getMessage());
	}

}
