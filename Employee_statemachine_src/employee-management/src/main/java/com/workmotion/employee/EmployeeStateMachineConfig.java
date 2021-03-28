package com.workmotion.employee;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;

import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeState;

@Configuration
@EnableStateMachineFactory
public class EmployeeStateMachineConfig extends EnumStateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {
	
	@Autowired
	private StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister;

	@Override
	public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config) throws Exception {
		config.withPersistence()
		.runtimePersister(stateMachineRuntimePersister).and().withConfiguration().listener(listener());
		
	}

	@Override
	public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
		states.withStates().initial(EmployeeState.ADDED).end(EmployeeState.ACTIVE)
				.states(EnumSet.allOf(EmployeeState.class));
	}

	@Override
	public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
		transitions
			.withExternal()
				.source(EmployeeState.ADDED)
				.target(EmployeeState.INCHECK)
				.event(EmployeeEvent.ADDED)
				.and()
				.withExternal()
					.source(EmployeeState.INCHECK)
					.target(EmployeeState.APPROVED)
					.event(EmployeeEvent.APPROVED)
				.and()
				.withExternal()
					.source(EmployeeState.APPROVED)
					.target(EmployeeState.ACTIVE)
					.event(EmployeeEvent.ACTIVATED);
	}
	
	@Bean
	public StateMachineListener<EmployeeState, EmployeeEvent> listener() {
		return new EmployeeStateMachineListener();
	}
	
	@Bean
	public StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister(
			JpaStateMachineRepository jpaStateMachineRepository) {
		return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
	}
	
	@Bean
	public StateMachineService<EmployeeState, EmployeeEvent> stateMachineService(
			StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory,
			StateMachineRuntimePersister<EmployeeState, EmployeeEvent, String> stateMachineRuntimePersister) {
		return new DefaultStateMachineService<EmployeeState, EmployeeEvent>(stateMachineFactory, stateMachineRuntimePersister);
	}
	
	
}
