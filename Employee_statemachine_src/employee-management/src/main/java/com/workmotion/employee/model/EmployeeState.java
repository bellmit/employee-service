package com.workmotion.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmployeeState {

	ADDED, INCHECK, APPROVED, ACTIVE;
}
