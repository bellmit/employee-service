package com.workmotion.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workmotion.employee.repository.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	
	
	Employee findByEmpCode(String empCode);
	
	Employee findByBusinessKey(String businessKey);

}
