package com.workmotion.employee.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import com.github.javafaker.Faker;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.repository.entity.Employee;
import com.workmotion.employee.repository.entity.EmployeeEvent;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class EmployeeEventRepositoryTest {

	@Autowired
	EmployeeEventRepository employeeEventRepository;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	private JdbcTemplate template;

	Faker faker = new Faker();

	String empCode = UUID.randomUUID().toString();

	@Test
	@DisplayName("Check default query behaviour")
	public void testDefaultSettings() throws Exception {
		assertThat(template.queryForObject("SELECT COUNT(*) from employee_event", Integer.class))
				.isGreaterThanOrEqualTo(0);
	}

	@Test
	@DisplayName("given Employee with new event when save return success")
	@Transactional
	public void givenEmployeeEventWhenSaveThenSuccess() {
		Employee employee = creatEmployeeEntity();
		employeeRepository.save(employee);
		EmployeeEvent employeeEvent = createNewOrderEvent(employee, EmployeeState.ADDED);
		employeeEvent = employeeEventRepository.save(employeeEvent);
		assertThat(employeeEvent.getId()).isGreaterThan(0);
	}

	private EmployeeEvent createNewOrderEvent(Employee employee, EmployeeState employeeState) {
		EmployeeEvent employeeEvent = new EmployeeEvent();
		employeeEvent.setEmployee(employee);
		employeeEvent.setCreationDate(LocalDateTime.now());
		employeeEvent.setEmployeeState(employeeState);
		return employeeEvent;
	}

	private Employee creatEmployeeEntity() {
		Employee employee = new Employee();
		employee.setFirstName(faker.name().firstName());
		employee.setLastName(faker.name().lastName());
		employee.setBirthDate(LocalDate.of(1989, 1, 14));
		employee.setHireDate(LocalDate.now());
		employee.setEmail(faker.internet().emailAddress());
		employee.setEmpCode(empCode);
		employee.setBusinessKey(empCode);
		employee.setCurrentState(EmployeeState.ADDED);
		employee.setGender(Gender.MALE);
		employee.setMobilePhone(faker.phoneNumber().cellPhone());
		return employee;
	}

}
