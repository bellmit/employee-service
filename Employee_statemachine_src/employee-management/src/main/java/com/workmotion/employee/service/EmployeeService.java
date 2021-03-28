package com.workmotion.employee.service;

import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.ResponseModel;

public interface EmployeeService {
	
	public ResponseModel addEmployee(EmployeeModel model);
	
	public ResponseModel changeEmployeeState(String empCode, EmployeeEvent event);

}
