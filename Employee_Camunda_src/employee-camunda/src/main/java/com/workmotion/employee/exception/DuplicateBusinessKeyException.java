package com.workmotion.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason="there is already an order with this business key")
public class DuplicateBusinessKeyException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DuplicateBusinessKeyException(String message) {
		super(message);
	}

}
