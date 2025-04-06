package com.bright.ems.controller;


import com.bright.ems.dto.request.EmployeePatchDto;
import com.bright.ems.dto.request.EmployeeRequestDto;
import com.bright.ems.dto.response.EmployeeResponseDto;
import com.bright.ems.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDto> createEmployee(@Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto employeeResponseDto = employeeService.createEmployee(employeeRequestDto).orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponseDto>> getEmployees() {
        List<EmployeeResponseDto> employeeResponseDtos = employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(employeeResponseDtos);
    }

    @PatchMapping("/{email}")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(@PathVariable String email, @Valid @RequestBody EmployeePatchDto employeePatchDto) {
        EmployeeResponseDto employeeResponseDto = employeeService.updateEmployeePartially(email, employeePatchDto).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK).body(employeeResponseDto);
    }

    @PutMapping("/{email}")
    public ResponseEntity<EmployeeResponseDto> updateEmployeeEmail(@PathVariable String email, @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {
        EmployeeResponseDto employeeResponseDto = employeeService.updateEmployee(email, employeeRequestDto).orElseThrow();
        return ResponseEntity.status(HttpStatus.OK).body(employeeResponseDto);
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String email) {
        employeeService.deleteEmployee(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
