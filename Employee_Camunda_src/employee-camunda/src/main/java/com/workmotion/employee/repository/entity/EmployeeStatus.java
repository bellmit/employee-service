package com.workmotion.employee.repository.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "employee_status")
@Data
public class EmployeeStatus implements Serializable {

	private static final long serialVersionUID = 7680478980037554111L;

	@Id
	@Column(name = "code")
	private String code;

	@Column(name = "name", unique = true, nullable = false)
	private String name;
}
