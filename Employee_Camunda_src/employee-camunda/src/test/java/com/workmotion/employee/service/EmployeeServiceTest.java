package com.workmotion.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.UUID;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.workmotion.employee.exception.InvalidEventException;
import com.workmotion.employee.exception.RequestNotFoundException;
import com.workmotion.employee.mapper.EmployeeMapper;
import com.workmotion.employee.mapper.EmployeeMapperImpl;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.TaskEvent;
import com.workmotion.employee.repository.EmployeeEventRepository;
import com.workmotion.employee.repository.EmployeeRepository;
import com.workmotion.employee.repository.entity.Employee;
import com.workmotion.employee.service.impl.DefaultEmployeeService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class EmployeeServiceTest {

	@Mock
	EmployeeRepository employeeRepository;

	@Mock
	BpmService bpmService;

	@Spy
	EmployeeMapper employeeMapper = new EmployeeMapperImpl();

	@Mock
	EmployeeEventRepository employeeEventRepository;

	@Mock
	ProcessEngine processEngine;
	
	@Mock
	TaskService taskService;
	
	@Mock
	TaskQuery taskQuery;
	
	@Mock
	Task task;
			

	@InjectMocks
	DefaultEmployeeService employeeService;

	Faker faker = new Faker();

	String empCode = UUID.randomUUID().toString();

	@Test
	@DisplayName("given valid employee request when save should sucess")
	public void givenValidEmployeeRequestReturnSucces() {
		EmployeeModel employeeModel = creatEmployeeModel();
		mockEmployeeRepository(employeeModel);
		ResponseModel response = employeeService.addEmployee(employeeModel);
		assertThat(response).isNotNull();
		assertThat(response.getEmpCode()).isNotNull();
		assertThat(response.getCurrentState()).isNotNull();
		verify(employeeRepository, atLeastOnce()).save(notNull());
		verify(bpmService, atLeastOnce()).startEmployeeProcess(notNull());
		verify(employeeEventRepository).save(argThat((event) -> event.getEmployeeState().equals(EmployeeState.ADDED)));
	}

	@Test
	@DisplayName("given event and empCode when change state should sucess")
	public void givenEventAndEmpCodeWhenChangeStateReturnSucces() {
		EmployeeModel employeeModel = creatEmployeeModel();
		String taskId = Integer.toString(faker.number().randomDigitNotZero());
		mockEmployeeFindByEmpCode(employeeModel);
		mockProcessEngineQuery();
		doReturn(task).when(taskQuery).singleResult();
		doReturn(taskId).when(task).getId();
		ResponseModel response = employeeService.changeEmployeeState(empCode, TaskEvent.ADDED);
		assertThat(response).isNotNull();
		assertThat(response.getEmpCode()).isNotNull();
		assertThat(response.getCurrentState()).isNotNull();
		verify(taskService, atLeastOnce()).complete(notNull());
		verify(employeeRepository, times(2)).findByEmpCode(notNull());
	}

	@Test
	@DisplayName("given invalid empCode when change state should throw requestNotFoundException")
	public void givenInvalidEmpCodeWhenChangeStateThrowNotFoundException() {
		Throwable thrown = catchThrowable(() -> {
			employeeService.changeEmployeeState(empCode, TaskEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(RequestNotFoundException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(taskService, never()).complete(notNull());
	}

	@Test
	@DisplayName("given event and empCode when employee is in active state should throw InvalidEventException")
	public void givenEventAndEmpCodeWhenEployeeStateActiveThrowInvalidEventException() {
		EmployeeModel employeeModel = creatEmployeeModel();
		employeeModel.setCurrentState(EmployeeState.ACTIVE);
		mockEmployeeFindByEmpCode(employeeModel);
		Throwable thrown = catchThrowable(() -> {
			employeeService.changeEmployeeState(empCode, TaskEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(InvalidEventException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, never()).save(notNull());
	}

	@Test
	@DisplayName("given invalid event and empCode when change state throw RequestNotFoundException")
	public void givenInvalidEventAndEmpCodeWhenChangeStateThrowRequestNotFoundException() {
		EmployeeModel employeeModel = creatEmployeeModel();
		mockEmployeeFindByEmpCode(employeeModel);
		mockProcessEngineQuery();
		doReturn(null).when(taskQuery).singleResult();
		Throwable thrown = catchThrowable(() -> {
			employeeService.changeEmployeeState(empCode, TaskEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(RequestNotFoundException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, never()).save(notNull());
		verify(taskService, never()).complete(notNull());
	}

	private void mockEmployeeRepository(EmployeeModel model) {
		Employee employeeEntity = employeeMapper.modelToEntity(model);
		when(employeeRepository.save(any())).thenReturn(creatEmployeeEntity(employeeEntity));
	}
	
	private void mockProcessEngineQuery() {
		doReturn(taskService).when(processEngine).getTaskService();
		doReturn(taskQuery).when(taskService).createTaskQuery();
		doReturn(taskQuery).when(taskQuery).active();
		doReturn(taskQuery).when(taskQuery).processVariableValueEquals(notNull(), notNull());
		doReturn(taskQuery).when(taskQuery).processInstanceBusinessKey(notNull());
	
	}

	private void mockEmployeeFindByEmpCode(EmployeeModel model) {
		Employee employeeEntity = employeeMapper.modelToEntity(model);
		when(employeeRepository.findByEmpCode(any())).thenReturn(creatEmployeeEntity(employeeEntity));
	}

	private EmployeeModel creatEmployeeModel() {
		EmployeeModel employeeModel = new EmployeeModel();
		employeeModel.setFirstName(faker.name().firstName());
		employeeModel.setLastName(faker.name().lastName());
		employeeModel.setBirthDate(LocalDate.of(1989, 1, 14));
		employeeModel.setHireDate(LocalDate.now());
		employeeModel.setEmail(faker.internet().emailAddress());
		employeeModel.setEmpCode(empCode);
		employeeModel.setBusinessKey(empCode);
		employeeModel.setCurrentState(EmployeeState.ADDED);
		employeeModel.setGender(Gender.MALE);
		employeeModel.setMobilePhone(faker.phoneNumber().cellPhone());
		return employeeModel;
	}

	private Employee creatEmployeeEntity(Employee employeeEntity) {
		employeeEntity.setId(faker.number().randomDigitNotZero());
		return employeeEntity;
	}

}
