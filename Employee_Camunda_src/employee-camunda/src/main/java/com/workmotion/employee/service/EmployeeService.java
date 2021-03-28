package com.workmotion.employee.service;

import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.TaskEvent;

public interface EmployeeService {
	
	public ResponseModel addEmployee(EmployeeModel model);
	
	public void addEmployeeEvent(String businessKey, EmployeeState state);
	
	public  ResponseModel changeEmployeeState(String empCode, TaskEvent event);

}
