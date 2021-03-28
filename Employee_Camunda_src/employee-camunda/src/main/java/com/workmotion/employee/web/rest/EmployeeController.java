package com.workmotion.employee.web.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.model.ResponseModel;
import com.workmotion.employee.model.StateEventRequest;
import com.workmotion.employee.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/employee")
@Log4j2
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;

	@PostMapping
	public ResponseEntity<ResponseModel> addEmployee(@RequestBody @Validated EmployeeModel employeeModel) {
		log.info("adding new employee {}", employeeModel);
		ResponseModel response = employeeService.addEmployee(employeeModel);
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{empCode}")
	public ResponseEntity<ResponseModel> changeEmployeeState(@PathVariable("empCode") String empCode,
			@RequestBody StateEventRequest eventRequest) {
		log.info("sent event {} for Employee with code {} ", eventRequest, empCode);
		ResponseModel response = employeeService.changeEmployeeState(empCode, eventRequest.getEvent());
		return ResponseEntity.ok(response);
	}

}
