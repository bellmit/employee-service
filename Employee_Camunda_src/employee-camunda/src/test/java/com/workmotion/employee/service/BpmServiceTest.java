package com.workmotion.employee.service;

import java.time.LocalDate;
import java.util.UUID;

import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.github.javafaker.Faker;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.model.TaskEvent;
import com.workmotion.employee.service.impl.DefaultBpmService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class BpmServiceTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	RuntimeService runtimeService;

	@InjectMocks
	BpmService bpmService = new DefaultBpmService();

	Faker faker = new Faker();
	String empCode = UUID.randomUUID().toString();
	
	@Test
	public void givenOrderModelWhenStartProcessThenSuccess() {
		EmployeeModel employeeModel = creatEmployeeModel();
		bpmService.startEmployeeProcess(employeeModel);
		Mockito.verify(runtimeService).createProcessInstanceByKey(Mockito.anyString());
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
		employeeModel.setNextEvent(TaskEvent.ADDED);
		employeeModel.setGender(Gender.MALE);
		employeeModel.setMobilePhone(faker.phoneNumber().cellPhone());
		return employeeModel;
	}

}
