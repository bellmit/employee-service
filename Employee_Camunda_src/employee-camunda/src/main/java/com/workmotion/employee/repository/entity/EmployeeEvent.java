package com.workmotion.employee.repository.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmotion.employee.model.EmployeeState;

import lombok.Data;

@Entity
@Table(name = "employee_event")
@Data
public class EmployeeEvent implements Serializable{
	
	
	private static final long serialVersionUID = 1686229569425151262L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@JoinColumn(name = "employee_id")
	@ManyToOne
	private Employee employee;
	
	@Column(name = "creation_date")
	private LocalDateTime creationDate;
	
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EmployeeState employeeState;
	
	@Column(name = "description")
	private String description;
	

}
