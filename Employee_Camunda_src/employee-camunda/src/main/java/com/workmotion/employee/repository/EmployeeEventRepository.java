package com.workmotion.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.workmotion.employee.repository.entity.EmployeeEvent;

@Repository
public interface EmployeeEventRepository extends JpaRepository<EmployeeEvent, Integer> {

	

}
