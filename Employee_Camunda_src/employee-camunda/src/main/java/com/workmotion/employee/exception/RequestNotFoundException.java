package com.workmotion.employee.exception;

public class RequestNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2207910888163044053L;
	
	public RequestNotFoundException() {
		super("data not found");
	}
	
	public RequestNotFoundException(String message) {
		super(message);
	}

}
