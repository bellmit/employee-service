package com.workmotion.employee.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.service.BpmService;

@Service
public class DefaultBpmService implements BpmService {

	private final String EMPLOYEE_MANAGMENT_PROCESS_KEY = "employee_management_process";

	@Autowired
	private RuntimeService runtimeService;

	@Override
	public void startEmployeeProcess(EmployeeModel employeeModel) {

		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.put("firstName", employeeModel.getFirstName());
		processVariables.put("lastName", employeeModel.getLastName());
		processVariables.put("CamundaBpmBusinessKey", employeeModel.getBusinessKey());
		processVariables.put("birthDate", employeeModel.getBirthDate().toString());
		processVariables.put("hireDate", employeeModel.getHireDate().toString());
		processVariables.put("email", employeeModel.getEmail());
		processVariables.put("mobilePhone", employeeModel.getMobilePhone());
		processVariables.put("currentState", employeeModel.getCurrentState().toString());
		processVariables.put("nextEvent", employeeModel.getNextEvent().toString());

		runtimeService.createProcessInstanceByKey(EMPLOYEE_MANAGMENT_PROCESS_KEY).setVariables(processVariables)
				.businessKey(employeeModel.getBusinessKey()).executeWithVariablesInReturn();
	}

}
