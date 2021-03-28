package com.workmotion.employee.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskEvent {

	ADDED,
	APPROVED,
	ACTIVATED;
}
