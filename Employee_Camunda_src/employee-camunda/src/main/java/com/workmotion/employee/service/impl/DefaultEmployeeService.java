package com.workmotion.employee.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workmotion.employee.exception.DuplicateBusinessKeyException;
import com.workmotion.employee.exception.InvalidEventException;
import com.workmotion.employee.exception.RequestNotFoundException;
import com.workmotion.employee.mapper.EmployeeMapper;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.TaskEvent;
import com.workmotion.employee.repository.EmployeeEventRepository;
import com.workmotion.employee.repository.EmployeeRepository;
import com.workmotion.employee.repository.entity.Employee;
import com.workmotion.employee.repository.entity.EmployeeEvent;
import com.workmotion.employee.service.BpmService;
import com.workmotion.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class DefaultEmployeeService implements EmployeeService {

	
	private final EmployeeRepository employeeRepository;
	
	private final EmployeeEventRepository employeeEventRepository;
	
	private final EmployeeMapper employeeMapper;
	
	private final BpmService bpmService;
	
	private final ProcessEngine processEngine;
	
	@Override
	public ResponseModel addEmployee(EmployeeModel model) {
		String businessKey = UUID.randomUUID().toString();
		
		Employee employee = employeeRepository.findByBusinessKey(businessKey);
		if(Objects.nonNull(employee)) {
			throw new DuplicateBusinessKeyException("employee with the same empCode already exists");
		}
		model.setBusinessKey(businessKey);
		model.setEmpCode(businessKey);
		model.setCurrentState(EmployeeState.ADDED);
		employee = employeeMapper.modelToEntity(model);
		employee = employeeRepository.save(employee);
		model.setId(employee.getId());
		model.setNextEvent(TaskEvent.ADDED);
		bpmService.startEmployeeProcess(model);
		
		addEvent(employee, EmployeeState.ADDED);
		ResponseModel responseModel = new ResponseModel(businessKey, EmployeeState.ADDED);
		return responseModel;
	}
	
	@Override
	public  ResponseModel changeEmployeeState(String empCode, TaskEvent event) {
		Employee employee =  employeeRepository.findByEmpCode(empCode);
		validateEmployee(employee);
		Task task = processEngine.getTaskService()
				.createTaskQuery()
				.active()
				.processVariableValueEquals("nextEvent", event.toString())
				.processInstanceBusinessKey(empCode)
				.singleResult();
		
		if (task == null){
			throw new RequestNotFoundException("Couldn't find a task for Event: " + event + ", Employee current state: " + employee.getCurrentState());
		}
		log.info("The following task id is going to be completed: {}", task.getId());
		processEngine.getTaskService().complete(task.getId());
		employee =  employeeRepository.findByEmpCode(empCode);
		log.info("employee with empCode {}, change state to: {}", empCode, employee.getCurrentState());
		return new ResponseModel(empCode, employee.getCurrentState());
		
	}
	
	private void validateEmployee(Employee employee) {
		if(Objects.isNull(employee)) {
			throw new RequestNotFoundException("coudln't find employee");
		}
		if(employee.getCurrentState().equals(EmployeeState.ACTIVE)) {
			throw new InvalidEventException("invalid event, employee in active state");
		}
	}
	
	@Override
	@Transactional
	public void addEmployeeEvent(String businessKey,  EmployeeState state) {
		Employee employee = employeeRepository.findByBusinessKey(businessKey);
		employee.setCurrentState(state);
		addEvent(employee, state);
	}
	
	private void addEvent(Employee employee,  EmployeeState state) {
		EmployeeEvent employeeEvent = new EmployeeEvent();
		employeeEvent.setEmployee(employee);
		employeeEvent.setCreationDate(LocalDateTime.now());
		employeeEvent.setEmployeeState(state);
		employeeEventRepository.save(employeeEvent);
	}
	
}
