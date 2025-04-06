package com.bright.ems.repository;

import com.bright.ems.model.Employee;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {

    private Employee employee;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .departmentCode("Compro")
                .build();
    }

    @Test
    @DisplayName("Test for creating a new employee")
    void givenEmployee_whenSaveEmployee_thenEmployeeCreated() {
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        Assertions.assertThat(savedEmployee.getId()).isNotNull();
        Assertions.assertThat(savedEmployee.getFirstName()).isEqualTo(employee.getFirstName());
        Assertions.assertThat(savedEmployee.getLastName()).isEqualTo(employee.getLastName());
        Assertions.assertThat(savedEmployee.getEmail()).isEqualTo(employee.getEmail());
        Assertions.assertThat(savedEmployee.getDepartmentCode()).isEqualTo(employee.getDepartmentCode());
    }

    @Test
    @DisplayName("Test for find by email")
    void givenEmployee_whenFindByEmail_thenReturnEmployee() {
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        Optional<Employee> employeeOptional = employeeRepository.findByEmail(savedEmployee.getEmail());
        assertTrue(employeeOptional.isPresent());
//        assertEquals(savedEmployee, employeeOptional.get());
        Employee foundEmployee = employeeOptional.get();
        assertEquals(savedEmployee.getFirstName(), foundEmployee.getFirstName());
        assertEquals(savedEmployee.getLastName(), foundEmployee.getLastName());
        assertEquals(savedEmployee.getEmail(), foundEmployee.getEmail());
        assertEquals(savedEmployee.getDepartmentCode(), foundEmployee.getDepartmentCode());
    }

    @Test
    @DisplayName("Test for finding employees by department code")
    void givenEmployees_whenFindByDepartmentCode_thenReturnEmployees() {
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .departmentCode("Compro")
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@gmail.com")
                .departmentCode("Compro")
                .build();
        Employee employee3 = Employee.builder()
                .firstName("Bob")
                .lastName("Smith")
                .email("bob.smith@gmail.com")
                .departmentCode("Sec")
                .build();
        employeeRepository.saveAllAndFlush(Arrays.asList(employee1, employee2, employee3));
        List<Employee> expectedEmployees = Arrays.asList(employee1, employee2);
        List<Employee> actualEmployees = employeeRepository.findByDepartmentCodeIgnoreCase("Compro");
        Assertions.assertThat(actualEmployees).containsExactlyInAnyOrderElementsOf(expectedEmployees);
    }

    @Test
    @DisplayName("Test for deleting an employee by email")
    void givenEmployee_whenDeleteByEmail_thenDeleted() {
        Employee savedEmployee = employeeRepository.save(employee);
        employeeRepository.deleteByEmail(savedEmployee.getEmail());
        entityManager.flush(); // Ensure deletion is executed
        // Verify that the employee is deleted
        Optional<Employee> employeeOptional = employeeRepository.findByEmail(savedEmployee.getEmail());
        assertFalse(employeeOptional.isPresent());
//        assert !employeeOptional.isPresent();
    }

    @Test
    @DisplayName("Test for saving an existing employee")
    void givenExistingEmployee_whenSave_thenNotSaved() {
        employeeRepository.save(employee);
        Employee employee2 = Employee.builder()
                .firstName("John")
                .lastName("Smith")
                .email("john.smith@gmail.com")
                .departmentCode("Compro")
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> employeeRepository.saveAndFlush(employee2));
    }

    @Test
    @DisplayName("Test for updating an existing employee")
    void givenExistingEmployee_whenUpdate_thenEmployeeUpdated() {
        // Save initial employee
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        // Find and update
        Optional<Employee> employeeOptional = employeeRepository.findByEmail(savedEmployee.getEmail());
        assertTrue(employeeOptional.isPresent());
        Employee foundEmployee = employeeOptional.get();
        foundEmployee.setDepartmentCode("Sec");
        employeeRepository.saveAndFlush(foundEmployee);
        // Fetch again and verify the update
        Optional<Employee> updatedOptional = employeeRepository.findById(foundEmployee.getId());
        assertTrue(updatedOptional.isPresent());
        Employee updatedEmployee = updatedOptional.get();
        assertEquals("Sec", updatedEmployee.getDepartmentCode());
    }
}