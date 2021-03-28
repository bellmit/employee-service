package com.workmotion.employee.exception;

public class InvalidEventException extends RuntimeException {
	
	private static final long serialVersionUID = 713020072012338781L;

	public InvalidEventException() {
		super("Invalid change event");
	}
	
	public InvalidEventException(String message) {
		super(message);
	}
}
