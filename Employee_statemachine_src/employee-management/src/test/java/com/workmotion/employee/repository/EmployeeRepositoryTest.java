package com.workmotion.employee.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.javafaker.Faker;
import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;
import com.workmotion.employee.repository.entity.Employee;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Tag("test")
public class EmployeeRepositoryTest {
	
	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	private JdbcTemplate template;
	
	Faker faker = new Faker();
	
	String empCode = UUID.randomUUID().toString();
	
	@Test
	@DisplayName("Check default query behaviour")
	public void testDefaultSettings() throws Exception {
		assertThat(template.queryForObject("SELECT COUNT(*) from employee", Integer.class)).isGreaterThanOrEqualTo(0);
	}
	
	
	@Test
	@DisplayName("when save employee should success")
	public void whenSaveEmployeeThenSuccess() throws Exception {
		Employee employee = creatEmployeeEntity();
		employee = employeeRepository.save(employee);
		assertThat(employee.getId()).isNotNull();
		assertThat(employee.getId()).isGreaterThan(0);
	}
	
	
	
	@Test
	@DisplayName("Get list of all employees Should return non Empty list")
	public void WhenFindAllThenReturnNonEmptyList() {
		Employee employee = creatEmployeeEntity();
		employee = employeeRepository.save(employee);
		List<Employee> employees = employeeRepository.findAll();
		assertThat(employees).isNotEmpty();
	}
	

	@Test
	@DisplayName("Get find employee by empCode Should return Employee")
	public void WhenFindByEmpCodeThenReturnSuccess() {
		Employee employee = creatEmployeeEntity();
		employeeRepository.save(employee);
		employee = employeeRepository.findByEmpCode(employee.getEmpCode());
		assertThat(employee.getId()).isNotNull();
		assertThat(employee.getId()).isGreaterThan(0);
	}
	
	@AfterEach
	public void cleanDatabase() {
		employeeRepository.deleteAll();
	}
	
	private Employee creatEmployeeEntity() {
		Employee employee = new Employee();
		employee.setFirstName(faker.name().firstName());
		employee.setLastName(faker.name().lastName());
		employee.setBirthDate(LocalDate.of(1989, 1, 14));
		employee.setHireDate(LocalDate.now());
		employee.setEmail(faker.internet().emailAddress());
		employee.setEmpCode(empCode);
		employee.setStateMachineId(UUID.randomUUID().toString());
		employee.setCurrentState(EmployeeState.ADDED);
		employee.setGender(Gender.MALE);
		employee.setMobilePhone(faker.phoneNumber().cellPhone());
		return employee;
	}

}
