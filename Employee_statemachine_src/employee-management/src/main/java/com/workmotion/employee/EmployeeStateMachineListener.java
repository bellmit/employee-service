package com.workmotion.employee;

import org.springframework.messaging.Message;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeState;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class EmployeeStateMachineListener extends StateMachineListenerAdapter<EmployeeState, EmployeeEvent>{

	@Override
	public void stateChanged(State<EmployeeState, EmployeeEvent> from, State<EmployeeState, EmployeeEvent> to) {
		log.info("State change to {}", to.getId());
	}
	
	@Override
	public void eventNotAccepted(Message<EmployeeEvent> event) {
		log.info("invalid event {}", event);
	}
}
