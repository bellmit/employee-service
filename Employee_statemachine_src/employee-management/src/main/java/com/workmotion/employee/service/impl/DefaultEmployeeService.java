package com.workmotion.employee.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.StateMachineEventResult.ResultType;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import com.workmotion.employee.exception.InvalidEventException;
import com.workmotion.employee.exception.RequestNotFoundException;
import com.workmotion.employee.mapper.EmployeeMapper;
import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.repository.EmployeeRepository;
import com.workmotion.employee.repository.entity.Employee;
import com.workmotion.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class DefaultEmployeeService implements EmployeeService {

	
	private final EmployeeRepository employeeRepository;
	
	private final EmployeeMapper employeeMapper;
	
	private final StateMachineService<EmployeeState, EmployeeEvent> stateMachineService;
	
	@Override
	public ResponseModel addEmployee(EmployeeModel model) {
		String machineId = UUID.randomUUID().toString();
		String empCode = UUID.randomUUID().toString();
		StateMachine<EmployeeState, EmployeeEvent> currentStateMachine = stateMachineService.acquireStateMachine(machineId);
		currentStateMachine.startReactively().block();
		model.setCurrentState(currentStateMachine.getState().getId());
		model.setStateMachineId(machineId);
		model.setEmpCode(empCode);
		Employee employee = employeeMapper.modelToEntity(model);
		employeeRepository.save(employee);
		log.info("employee added with empCode {} and current state of {}", employee.getEmpCode(), employee.getCurrentState());
		return new ResponseModel(empCode, model.getCurrentState());
	}

	@Override
	public  ResponseModel changeEmployeeState(String empCode, EmployeeEvent event) {
		Employee employee =  employeeRepository.findByEmpCode(empCode);
		validateEmployee(employee);
		StateMachineEventResult<EmployeeState, EmployeeEvent> result = getNextEmployeeState(employee,  event) ;
		EmployeeState currentState = result.getRegion().getState().getId();
		employee.setCurrentState(currentState);
		employeeRepository.save(employee);
		log.info("employee with empCode {} changed state to {}", employee.getEmpCode(), employee.getCurrentState());
		return new ResponseModel(empCode, currentState);
	}
	
	private void validateEmployee(Employee employee) {
		if(Objects.isNull(employee)) {
			throw new RequestNotFoundException("coudln't find employee");
		}
		if(employee.getCurrentState().equals(EmployeeState.ACTIVE)) {
			throw new InvalidEventException("invalid event, employee in active state");
		}
	}
	
	private StateMachineEventResult<EmployeeState, EmployeeEvent> getNextEmployeeState(Employee employee,  EmployeeEvent event) {
		StateMachine<EmployeeState, EmployeeEvent> currentStateMachine = stateMachineService.acquireStateMachine(employee.getStateMachineId());
		Mono<Message<EmployeeEvent>> message = Mono.just(MessageBuilder.withPayload(event).build());
		StateMachineEventResult<EmployeeState, EmployeeEvent> result = currentStateMachine.sendEvent(message).blockFirst();
		ResultType resultType =  result.getResultType();
		if(resultType.equals(ResultType.DENIED)) {
			String allowedEvent = getCurrentAllowedEevent(currentStateMachine);
			throw new InvalidEventException("invalid event, current allowed event is: " + allowedEvent);
		}
		if(currentStateMachine.isComplete()) {
			stateMachineService.releaseStateMachine(employee.getStateMachineId());
		}
		return result;
	}
	
	private String getCurrentAllowedEevent(StateMachine<EmployeeState, EmployeeEvent> currentStateMachine) {
		EmployeeState currentState = currentStateMachine.getState().getId();
		List<Transition<EmployeeState, EmployeeEvent>> transitions = currentStateMachine.getTransitions().stream()
				.filter(t -> t.getSource().getId().equals(currentState)).collect(Collectors.toList());
		if(transitions != null & !transitions.isEmpty()) {
			return transitions.get(0).getTrigger().getEvent().toString();
		}
		return "none";
		
	}

}
