package com.workmotion.employee.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.workmotion.employee.model.EmployeeModel;
import com.workmotion.employee.repository.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

	EmployeeModel entityToModel(Employee entity);
	
	Employee modelToEntity(EmployeeModel model);
	
	List<EmployeeModel> entityListToModelList(List<Employee> entityList);
	
	List<Employee> ModelListToEntityList(List<EmployeeModel> modelList);

}
