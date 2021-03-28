package com.workmotion.employee.model;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EmployeeModel {

	@Schema(hidden = true)
	private Integer id;
	
	@Schema(hidden = true)
	private String empCode;
	
	@Schema(example = "farouk")
	@NotBlank
	private String firstName;
	
	@Schema(example = "elabady")
	@NotBlank
	private String lastName;
	
	@Schema(example = "farouk@example.com")
	@NotBlank
	private String email;
	
	@Schema(example = "00201001033180")
	private String mobilePhone;
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@Schema(example = "1989-01-14")
	@NotNull
	private LocalDate birthDate;
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	@Schema(example = "2021-03-27")
	@NotNull
	private LocalDate hireDate;
	
	@Schema(example = "MALE")
	private Gender gender;
	
	@Schema(hidden = true)
	private EmployeeState currentState;
	
	@Schema(hidden = true)
	private TaskEvent nextEvent;
	
	@Schema(hidden = true)
	private String businessKey;
	
}
