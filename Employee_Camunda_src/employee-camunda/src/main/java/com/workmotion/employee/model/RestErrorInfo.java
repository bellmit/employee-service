package com.workmotion.employee.model;

public class RestErrorInfo {
	public final String detail;
	public final String message;

	public RestErrorInfo(String message, String detail) {
		this.message = message;
		this.detail = detail;
	}
	
	public RestErrorInfo(String message) {
		this.message = message;
		this.detail = "";
	}
}
