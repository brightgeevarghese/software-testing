package com.bright.ems.service;

import com.bright.ems.dto.request.EmployeePatchDto;
import com.bright.ems.dto.request.EmployeeRequestDto;
import com.bright.ems.dto.response.EmployeeResponseDto;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Optional<EmployeeResponseDto> createEmployee(EmployeeRequestDto employeeRequestDto);
    List<EmployeeResponseDto> getAllEmployees();
    List<EmployeeResponseDto> findByFirstName(String firstName);
    List<EmployeeResponseDto> findByLastName(String lastName);
    List<EmployeeResponseDto> findByDepartmentCode(String departmentCode);
    Optional<EmployeeResponseDto> findByEmail(String email);
    Optional<EmployeeResponseDto> updateEmployee(String email, EmployeeRequestDto employeeRequestDto);
    Optional<EmployeeResponseDto> updateEmployeePartially(String email, EmployeePatchDto employeePatchDto);
    void deleteEmployee(String email);
}
