package com.workmotion.employee.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.StateMachineEventResult.ResultType;
import org.springframework.statemachine.region.Region;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import org.springframework.statemachine.trigger.Trigger;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.workmotion.employee.exception.InvalidEventException;
import com.workmotion.employee.exception.RequestNotFoundException;
import com.workmotion.employee.mapper.EmployeeMapper;
import com.workmotion.employee.mapper.EmployeeMapperImpl;
import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.repository.EmployeeRepository;
import com.workmotion.employee.repository.entity.Employee;
import com.workmotion.employee.service.impl.DefaultEmployeeService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Tag("test")
@SuppressWarnings("unchecked")
public class EmployeeServiceTest {

	@Mock
	EmployeeRepository employeeRepository;

	@Spy
	EmployeeMapper employeeMapper = new EmployeeMapperImpl();

	@Mock
	StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
	
	@Mock
	StateMachine<EmployeeState, EmployeeEvent> stateMachine;
	
	@Mock
	State<EmployeeState, EmployeeEvent> state;
	@Mock
	StateMachineEventResult<EmployeeState, EmployeeEvent> stateMachineResult;
	
	@Mock
	Region<EmployeeState, EmployeeEvent> region;
	
	@Mock
	Transition<EmployeeState, EmployeeEvent> transition;
	
	@Mock
	Trigger<EmployeeState, EmployeeEvent> trigger;
	
	@InjectMocks
	DefaultEmployeeService employeeService;
	
	Faker faker = new Faker();
	
	String empCode = UUID.randomUUID().toString();
	String machineId = UUID.randomUUID().toString();
	
	@Test
	@DisplayName("given valid employee request when save should sucess")
	public void givenValidEmployeeRequestReturnSucces() {
		EmployeeModel employeeModel =creatEmployeeModel();
		mockStateMachine();
		mockStateMachineService();
		mockState(EmployeeState.ADDED);
		mockEmployeeRepository(employeeModel);
		ResponseModel response = employeeService.addEmployee(employeeModel);
		
		assertThat(response).isNotNull();
		assertThat(response.getEmpCode()).isNotNull();
		assertThat(response.getCurrentState()).isNotNull();
		verify(employeeRepository, atLeastOnce()).save(notNull());
		verify(stateMachine, atLeastOnce()).startReactively();
		verify(stateMachine, atLeastOnce()).getState();
		verify(state, atLeastOnce()).getId();
	}
	
	
	@Test
	@DisplayName("given event and empCode when change state should sucess")
	public void givenEventAndEmpCodeWhenChangeStateReturnSucces() {
		EmployeeModel employeeModel =creatEmployeeModel();
		doReturn(region).when(stateMachineResult).getRegion();
		doReturn(state).when(region).getState();
		mockState(EmployeeState.INCHECK);
		mockStateMachineService();
		mockStateMachineSendEventSuccess();
		mockEmployeeRepository(employeeModel);
		mockEmployeeFindByCode(employeeModel);
		ResponseModel response = employeeService.changeEmployeeState(empCode, EmployeeEvent.ADDED);
		assertThat(response).isNotNull();
		assertThat(response.getEmpCode()).isNotNull();
		assertThat(response.getCurrentState()).isNotNull();
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, atLeastOnce()).save(notNull());
		verify(stateMachine, atLeastOnce()).sendEvent(any(Mono.class));
		verify(region, atLeastOnce()).getState();
		verify(state, atLeastOnce()).getId();
	}
	
	@Test
	@DisplayName("given active event and empCode when change state should sucess")
	public void givenActiveEventAndEmpCodeWhenChangeStateReturnSucces() {
		EmployeeModel employeeModel =creatEmployeeModel();
		doReturn(region).when(stateMachineResult).getRegion();
		doReturn(state).when(region).getState();
		doReturn(true).when(stateMachine).isComplete();
		mockState(EmployeeState.ACTIVE);
		mockStateMachineService();
		mockStateMachineSendEventSuccess();
		mockEmployeeRepository(employeeModel);
		mockEmployeeFindByCode(employeeModel);
		ResponseModel response = employeeService.changeEmployeeState(empCode, EmployeeEvent.ACTIVATED);
		assertThat(response).isNotNull();
		assertThat(response.getEmpCode()).isNotNull();
		assertThat(response.getCurrentState()).isNotNull();
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, atLeastOnce()).save(notNull());
		verify(stateMachine, atLeastOnce()).sendEvent(any(Mono.class));
		verify(region, atLeastOnce()).getState();
		verify(state, atLeastOnce()).getId();
		verify(stateMachineService, atLeastOnce()).releaseStateMachine(notNull());
	}
	
	@Test
	@DisplayName("given invalid empCode when change state should throw requestNotFoundException")
	public void givenInvalidEmpCodeWhenChangeStateThrowNotFoundException() {
		Throwable thrown = catchThrowable(() -> { 
			employeeService.changeEmployeeState(empCode, EmployeeEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(RequestNotFoundException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, never()).save(notNull());
		verify(stateMachine, never()).sendEvent(any(Mono.class));
	}
	
	@Test
	@DisplayName("given event and empCode when employee is in active state should throw InvalidEventException")
	public void givenEventAndEmpCodeWhenEployeeStateActiveThrowInvalidEventException() {
		EmployeeModel employeeModel = creatEmployeeModel();
		employeeModel.setCurrentState(EmployeeState.ACTIVE);
		mockEmployeeFindByCode(employeeModel);
		Throwable thrown = catchThrowable(() -> { 
			employeeService.changeEmployeeState(empCode, EmployeeEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(InvalidEventException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, never()).save(notNull());
		verify(stateMachine, never()).sendEvent(any(Mono.class));
	}
	
	
	@Test
	@DisplayName("given invalid event and empCode when change state throw InvalidEventException")
	public void givenInvalidEventAndEmpCodeWhenChangeStateThrowInvalidEventException() {
		EmployeeModel employeeModel =creatEmployeeModel();
		doReturn(state).when(stateMachine).getState();
		mockState(EmployeeState.INCHECK);
		mockStateMachineService();
		mockStateMachineSendEventFailed();
		mockEmployeeFindByCode(employeeModel);
		Throwable thrown = catchThrowable(() -> { 
			employeeService.changeEmployeeState(empCode, EmployeeEvent.ADDED);
		});
		assertThat(thrown).isInstanceOf(InvalidEventException.class);
		verify(employeeRepository, atLeastOnce()).findByEmpCode(notNull());
		verify(employeeRepository, never()).save(notNull());
		verify(stateMachine, atLeastOnce()).sendEvent(any(Mono.class));
		verify(state, atLeastOnce()).getId();
	}
	
	private void mockStateMachineService() {
		doReturn(stateMachine).when(stateMachineService).acquireStateMachine(any());
	}
	
	private void mockStateMachine() {
		doReturn(Mono.empty()).when(stateMachine).startReactively();
		doReturn(state).when(stateMachine).getState();
	}
	
	private void mockStateMachineSendEventSuccess() {
		doReturn(Flux.just(stateMachineResult)).when(stateMachine).sendEvent(any(Mono.class));
		doReturn(ResultType.ACCEPTED).when(stateMachineResult).getResultType();
	}
	
	private void mockStateMachineSendEventFailed() {
		doReturn(Flux.just(stateMachineResult)).when(stateMachine).sendEvent(any(Mono.class));
		doReturn(ResultType.DENIED).when(stateMachineResult).getResultType();
		List<Transition<EmployeeState, EmployeeEvent>> transitions = new ArrayList<>();
		transitions.add(transition);
		doReturn(transitions).when(stateMachine).getTransitions();
		doReturn(state).when(transition).getSource();
		doReturn(trigger).when(transition).getTrigger();
		doReturn(EmployeeEvent.APPROVED).when(trigger).getEvent();
		
	}
	
	private void mockState(EmployeeState currentState) {
		doReturn(currentState).when(state).getId();
	}
	
	private void mockEmployeeRepository(EmployeeModel model) {
		Employee employeeEntity = employeeMapper.modelToEntity(model);
		when(employeeRepository.save(any())).thenReturn(creatEmployeeEntity(employeeEntity));
	}
	
	private void mockEmployeeFindByCode(EmployeeModel model) {
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
		employeeModel.setStateMachineId(machineId);
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
