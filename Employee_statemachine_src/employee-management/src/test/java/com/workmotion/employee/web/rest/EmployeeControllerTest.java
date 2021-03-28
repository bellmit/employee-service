package com.workmotion.employee.web.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.workmotion.employee.exception.InvalidEventException;
import com.workmotion.employee.exception.RequestNotFoundException;
import com.workmotion.employee.model.EmployeeEvent;
import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.StateEventRequest;
import com.workmotion.employee.service.EmployeeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class EmployeeControllerTest {
	
	@MockBean
	private EmployeeService service;

	@Autowired
	private MockMvc mockMvc;
	
	String empCode = UUID.randomUUID().toString();
	
	Faker faker = new Faker();
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Test
	@DisplayName("POST /employee success")
	void GivenEmployeeWhenAddReturnSuccess() throws Exception {
		// Setup our mocked service
		ResponseModel response = new ResponseModel(empCode, EmployeeState.ADDED);
		doReturn(response).when(service).addEmployee(isNotNull());
		// Execute the POST request
		mockMvc.perform(post("/employee")
				.accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(asJsonString(creatEmployeeModel()))
						).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['empCode']", is(empCode)))
				.andExpect(jsonPath("$['currentState']", is(EmployeeState.ADDED.toString())));
	}
	
	@Test
	@DisplayName("PATCH /employee/{empCode} success")
	void GivenStateEventWhenChangeEmployeeStateReturnSuccess() throws Exception {
		// Setup our mocked service
		ResponseModel response = new ResponseModel(empCode, EmployeeState.INCHECK);
		doReturn(response).when(service).changeEmployeeState(isNotNull(), isNotNull());
		// Execute the patch request
		mockMvc.perform(patch("/employee/" + empCode)
				.accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(asJsonString(createStateEvent()))
						).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['empCode']", is(empCode)))
				.andExpect(jsonPath("$['currentState']", is(EmployeeState.INCHECK.toString())));
	}
	
	@Test
	@DisplayName("PATCH /employee/{empCode} invalid empCode")
	void GivenStateEventWhenChangeEmployeeStateThrowRequestNotFoundException() throws Exception {
		// Setup our mocked service
		doThrow(RequestNotFoundException.class).when(service).changeEmployeeState(isNotNull(), isNotNull());
		// Execute the patch request
		mockMvc.perform(patch("/employee/" + empCode)
				.accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(asJsonString(createStateEvent()))
						).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isNotFound())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
	}
	
	@Test
	@DisplayName("PATCH /employee/{empCode} invalid event")
	void GivenStateEventWhenChangeEmployeeStateThrowInvalidEventException() throws Exception {
		// Setup our mocked service
		doThrow(InvalidEventException.class).when(service).changeEmployeeState(isNotNull(), isNotNull());
		// Execute the Patch request
		mockMvc.perform(patch("/employee/" + empCode)
				.accept((MediaType.APPLICATION_JSON))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(asJsonString(createStateEvent()))
						).andDo(print())
				// Validate the response code and content type
				.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				// Validate the returned fields
				.andExpect(jsonPath("$['message']", IsAnything.anything()));
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
	
	private StateEventRequest createStateEvent() {
		StateEventRequest event = new StateEventRequest();
		event.setEvent(EmployeeEvent.ADDED);
		
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
