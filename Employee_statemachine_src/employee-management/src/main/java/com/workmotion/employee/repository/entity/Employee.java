package com.workmotion.employee.repository.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.workmotion.employee.model.EmployeeState;
import com.workmotion.employee.model.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "emp_code")
	private String empCode;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "mobile_phone")
	private String mobilePhone;
	
	@Column(name = "birth_date")
	private LocalDate birthDate;
	
	@Column(name = "hire_date")
	private LocalDate hireDate;
	
	@Column(name = "gender")
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Column(name = "current_state")
	@Enumerated(EnumType.STRING)
	private EmployeeState currentState;
	
	@Column(name = "state_machine_id")
	private String stateMachineId;
	
	
}
