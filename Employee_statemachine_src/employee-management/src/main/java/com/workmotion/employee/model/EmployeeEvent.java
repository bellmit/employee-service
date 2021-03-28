package com.workmotion.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmployeeEvent {

	ADDED,
	APPROVED,
	ACTIVATED;
}
