package com.workmotion.employee.task;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.TaskEvent;
import com.workmotion.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class EmployeeCheckedDelegate implements TaskListener {

	private final EmployeeService employeeService;

	@Override
	public void notify(DelegateTask delegateTask) {
		log.info("Process Ended with recieved variables: {}", delegateTask.getVariables());
		String businessKey = (String) delegateTask.getVariables().get("CamundaBpmBusinessKey");
		delegateTask.setVariable("currentState", EmployeeState.APPROVED.toString());
		delegateTask.setVariable("nextEvent", TaskEvent.ACTIVATED.toString());
		employeeService.addEmployeeEvent(businessKey, EmployeeState.APPROVED);
	}

}
