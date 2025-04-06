package com.bright.ems.service.impl;

import com.bright.ems.dto.request.EmployeePatchDto;
import com.bright.ems.dto.request.EmployeeRequestDto;
import com.bright.ems.dto.response.EmployeeResponseDto;
import com.bright.ems.exception.employee.DuplicateEmailException;
import com.bright.ems.exception.employee.EmployeeNotFoundException;
import com.bright.ems.model.Employee;
import com.bright.ems.repository.EmployeeRepository;
import com.bright.ems.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<EmployeeResponseDto> createEmployee(EmployeeRequestDto employeeRequestDto) {
        if (employeeRepository.findByEmail(employeeRequestDto.email()).isPresent()) {
            throw new DuplicateEmailException("Employee already exists with email: " + employeeRequestDto.email());
        }
        Employee employee = new Employee(
                employeeRequestDto.firstName(),
                employeeRequestDto.lastName(),
                employeeRequestDto.email(),
                employeeRequestDto.departmentCode()
        );
        Employee saved = employeeRepository.save(employee);
        return Optional.of(mapToResponse(saved));
    }

    private EmployeeResponseDto mapToResponse(Employee e) {
        return new EmployeeResponseDto(
                e.getFirstName(),
                e.getLastName(),
                e.getDepartmentCode()
        );
    }

    @Override
    public List<EmployeeResponseDto> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDto> findByFirstName(String firstName) {
        return employeeRepository.findByFirstNameIgnoreCase(firstName).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDto> findByLastName(String lastName) {
        return employeeRepository.findByLastNameIgnoreCase(lastName).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<EmployeeResponseDto> findByDepartmentCode(String departmentCode) {
        return employeeRepository.findByDepartmentCodeIgnoreCase(departmentCode).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeResponseDto> findByEmail(String email) {
        return employeeRepository.findByEmail(email).stream().map(this::mapToResponse).findFirst();
    }

    @Override
    public Optional<EmployeeResponseDto> updateEmployee(String email, EmployeeRequestDto employeeRequestDto) {
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException(email));
        employee.setFirstName(employeeRequestDto.firstName());
        employee.setLastName(employeeRequestDto.lastName());
        employee.setDepartmentCode(employeeRequestDto.departmentCode());
        employee.setEmail(email);
        employeeRepository.save(employee);
        return Optional.of(mapToResponse(employee));
    }

    @Override
    public Optional<EmployeeResponseDto> updateEmployeePartially(String email, EmployeePatchDto employeePatchDto) {
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException(email));
        if (employeePatchDto.firstName() != null) {
            employee.setFirstName(employeePatchDto.firstName());
        }
        if (employeePatchDto.lastName() != null) {
            employee.setLastName(employeePatchDto.lastName());
        }
        if (employeePatchDto.departmentCode() != null) {
            employee.setDepartmentCode(employeePatchDto.departmentCode());
        }
        employeeRepository.save(employee);
        return Optional.of(mapToResponse(employee));
    }

    @Override
    public void deleteEmployee(String email) {
        employeeRepository.findByEmail(email).orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));//404
        employeeRepository.deleteByEmail(email);
    }
}
