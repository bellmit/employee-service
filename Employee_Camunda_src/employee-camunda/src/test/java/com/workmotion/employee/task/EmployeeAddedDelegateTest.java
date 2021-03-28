package com.workmotion.employee.task;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.TaskEvent;
import com.workmotion.employee.service.EmployeeService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class EmployeeAddedDelegateTest {

	@Mock
	EmployeeService employeeService;
	

	@Mock
	DelegateTask delegateTask;

	@InjectMocks
	EmployeeAddedDelegate employeeAddedDelegate;
	
	String businessKey = UUID.randomUUID().toString();

	@BeforeEach
	public void init() throws IOException {
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("CamundaBpmBusinessKey", businessKey);
		doReturn(requestMap).when(delegateTask).getVariables();
	}

	@Test
	public void testJavaServiceDelegation() throws Exception {
		employeeAddedDelegate.notify(delegateTask);
		verify(delegateTask).setVariable(argThat((event) -> event.equals("currentState")),
				same(EmployeeState.INCHECK.toString()));
		verify(delegateTask).setVariable(argThat((event) -> event.equals("nextEvent")),
				same(TaskEvent.APPROVED.toString()));
		verify(employeeService, times(1)).addEmployeeEvent(notNull(), same(EmployeeState.INCHECK));

	}
}
