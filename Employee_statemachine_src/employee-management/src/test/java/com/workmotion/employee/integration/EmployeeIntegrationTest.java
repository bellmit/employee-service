package com.workmotion.employee.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.UUID;

import org.hamcrest.core.IsAnything;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.StateEventRequest;
import com.workmotion.employee.repository.EmployeeRepository;
import com.workmotion.employee.repository.entity.Employee;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("test")
public class EmployeeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private EmployeeRepository employeeRepostiroy;

	String empCode = UUID.randomUUID().toString();

	ObjectMapper mapper = new ObjectMapper();
	
	Faker faker = new Faker();

	@Test
	@DisplayName("POST /employee success")
	void GivenEmployeeWhenAddReturnSuccess() throws Exception {
		// Execute the POST request
		addNewEmployee();
	}
	
	@Test
	@DisplayName("PATCH /employee/{empCode} change state to activate should success")
	void GivenEmployeeWhenChangeStateToActivateReturnSuccess() throws Exception {
		// Execute the POST request
		ResponseModel responseModel =  addNewEmployee();
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.ADDED, EmployeeState.INCHECK);
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.APPROVED, EmployeeState.APPROVED);
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.ACTIVATED, EmployeeState.ACTIVE);
		Employee employee = employeeRepostiroy.findByEmpCode(responseModel.getEmpCode());
		assertThat(employee).isNotNull();
		assertThat(employee.getEmpCode()).isEqualTo(responseModel.getEmpCode());
		assertThat(employee.getCurrentState()).isEqualTo(EmployeeState.ACTIVE);		
	}
	
	@Test
	@DisplayName("PATCH /employee/{empCode} change state  after reaching end activate throw invalidEventException")
	void GivenEmployeeWhenChangeStateAfterActiveThrowInvalidEvent() throws Exception {
		// Execute the POST request
		ResponseModel responseModel =  addNewEmployee();
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.ADDED, EmployeeState.INCHECK);
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.APPROVED, EmployeeState.APPROVED);
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.ACTIVATED, EmployeeState.ACTIVE);
		
		mockMvc.perform(patch("/employee/" + empCode).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(EmployeeEvent.ADDED)))).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
		
		mockMvc.perform(patch("/employee/" + empCode).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(EmployeeEvent.APPROVED)))).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
		
		mockMvc.perform(patch("/employee/" + empCode).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(EmployeeEvent.ACTIVATED)))).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
	}

	@Test
	@DisplayName("PATCH /employee/{empCode} success")
	void GivenStateEventWhenChangeEmployeeStateReturnSuccess() throws Exception {
		ResponseModel responseModel =  addNewEmployee();
		changeEmployeeStateSucccess(responseModel, EmployeeEvent.ADDED, EmployeeState.INCHECK);
	}

	@Test
	@DisplayName("PATCH /employee/{empCode} invalid empCode")
	void GivenStateEventWhenChangeEmployeeStateThrowRequestNotFoundException() throws Exception {
		String empCode = UUID.randomUUID().toString();
		// Execute the patch request
		mockMvc.perform(patch("/employee/" + empCode).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(EmployeeEvent.ADDED)))).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isNotFound()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
	}

	@Test
	@DisplayName("PATCH /employee/{empCode} invalid event")
	void GivenStateEventWhenChangeEmployeeStateThrowInvalidEventException() throws Exception {
		ResponseModel responseModel =  addNewEmployee();
		// Execute the Patch request
		mockMvc.perform(patch("/employee/" + responseModel.getEmpCode()).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(EmployeeEvent.ACTIVATED)))).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isBadRequest()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
	}

	private ResponseModel addNewEmployee() throws Exception {
		ResponseModel responseModel = null;
		MvcResult result = mockMvc
				.perform(post("/employee").accept((MediaType.APPLICATION_JSON))
						.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(creatEmployeeModel())))
				.andDo(print())
				// Validate the response code and content type
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['empCode']", notNullValue()))
				.andExpect(jsonPath("$['currentState']", is(EmployeeState.ADDED.toString()))).andReturn();
		responseModel = mapper.readValue(result.getResponse().getContentAsString(), ResponseModel.class);
		return responseModel;
	}
	
	private void changeEmployeeStateSucccess(ResponseModel responseModel, EmployeeEvent event, EmployeeState nextState) throws Exception {
		mockMvc.perform(patch("/employee/" + responseModel.getEmpCode()).accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE).content(asJsonString(createStateEvent(event)))).andDo(print())
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$['empCode']", is(responseModel.getEmpCode())))
				.andExpect(jsonPath("$['currentState']", is(nextState.toString())));
	}

	private String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private StateEventRequest createStateEvent(EmployeeEvent employeeEvent) {
		StateEventRequest event = new StateEventRequest();
		event.setEvent(employeeEvent);
		return event;
	}

	private EmployeeModel creatEmployeeModel() {
		EmployeeModel employeeModel = new EmployeeModel();
		employeeModel.setFirstName(faker.name().firstName());
		employeeModel.setLastName(faker.name().lastName());
		employeeModel.setBirthDate(LocalDate.of(1989, 1, 14));
		employeeModel.setHireDate(LocalDate.now());
		employeeModel.setEmail(faker.internet().emailAddress());
		employeeModel.setEmpCode(empCode);
		employeeModel.setCurrentState(EmployeeState.ADDED);
		employeeModel.setGender(Gender.MALE);
		employeeModel.setMobilePhone(faker.phoneNumber().cellPhone());
		return employeeModel;
	}

}
