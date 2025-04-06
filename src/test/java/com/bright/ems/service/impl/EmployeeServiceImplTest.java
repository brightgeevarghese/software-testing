package com.bright.ems.service.impl;

import com.bright.ems.dto.request.EmployeePatchDto;
import com.bright.ems.dto.request.EmployeeRequestDto;
import com.bright.ems.dto.response.EmployeeResponseDto;
import com.bright.ems.exception.employee.DuplicateEmailException;
import com.bright.ems.exception.employee.EmployeeNotFoundException;
import com.bright.ems.model.Employee;
import com.bright.ems.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.hibernate.action.internal.EntityActionVetoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Employee anotherEmployee;
    private EmployeeRequestDto employeeRequestDto;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .departmentCode("Compro")
                .build();
        anotherEmployee = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@smith.com")
                .departmentCode("HR")
                .build();
        employeeRequestDto = new EmployeeRequestDto("John", "Doe", "john@doe.com", "Compro");
    }

    @Test
    @DisplayName("Create employee when email does not exist should return response DTO")
    void createEmployee_whenEmailDoesNotExist_shouldCreateAndReturnEmployeeResponseDto() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.empty());
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        Optional<EmployeeResponseDto> employeeResponseDto = employeeService.createEmployee(employeeRequestDto);
        assertTrue(employeeResponseDto.isPresent());
        Assertions.assertThat(employeeResponseDto.get()).isEqualTo(mapToEmployeeResponseDto(employee));
    }

    @Test
    @DisplayName("Create employee when email exists should throw DuplicateEmailException")
    void createEmployee_whenEmailExists_shouldReturnEmpty() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        assertThrows(DuplicateEmailException.class, () -> employeeService.createEmployee(employeeRequestDto));
    }

    @Test
    @DisplayName("Get all employees should return a list of DTOs")
    void getAllEmployees_shouldReturnEmployeeResponseDtos() {
        Mockito.when(employeeRepository.findAll()).thenReturn(List.of(employee, anotherEmployee));
        List<EmployeeResponseDto> employeeResponseDtos = employeeService.getAllEmployees();
        Assertions.assertThat(employeeResponseDtos.size()).isEqualTo(2);
        Assertions.assertThat(employeeResponseDtos).containsExactly(mapToEmployeeResponseDto(employee), mapToEmployeeResponseDto(anotherEmployee));
//        Assertions.assertThat(employeeResponseDtos.get(0)).isEqualTo(mapToEmployeeResponseDto(employee));
//        Assertions.assertThat(employeeResponseDtos.get(1)).isEqualTo(mapToEmployeeResponseDto(anotherEmployee));
    }

    @Test
    @DisplayName("Find by email when exists should return dto")
    void findByEmail_whenExists_shouldReturnEmployeeResponseDto() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        Optional<EmployeeResponseDto> employeeResponseDto = employeeService.findByEmail(employeeRequestDto.email());
        Assertions.assertThat(employeeResponseDto).isPresent();
        Assertions.assertThat(employeeResponseDto.get()).isEqualTo(mapToEmployeeResponseDto(employee));
    }

    @Test
    @DisplayName("Delete employee when exists should call deleteByEmail")
    void deleteEmployee_whenExists_shouldDelete() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        employeeService.deleteEmployee(employeeRequestDto.email());
        Mockito.verify(employeeRepository, Mockito.times(1)).deleteByEmail(employeeRequestDto.email());
        Mockito.verify(employeeRepository).findByEmail(employeeRequestDto.email());
    }

    @Test
    @DisplayName("Delete employee when not found should throw exception")
    void deleteEmployee_whenNotFound_shouldThrowException() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.empty());
//        employeeService.deleteEmployee(employeeRequestDto.email());//it should throw EntityNotFoundException
        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(employeeRequestDto.email()));
    }

    @Test
    @DisplayName("Find employees by first name should return matching Dtos")
    void findEmployees_byFirstName_shouldReturnEmployeeResponseDtos() {
        Mockito.when(employeeRepository.findByFirstNameIgnoreCase(employeeRequestDto.firstName())).thenReturn(List.of(employee));
        List<EmployeeResponseDto> employeeResponseDtos = employeeService.findByFirstName(employeeRequestDto.firstName());
        Assertions.assertThat(employeeResponseDtos.size()).isEqualTo(1);
        Assertions.assertThat(employeeResponseDtos).containsExactly(mapToEmployeeResponseDto(employee));
        Assertions.assertThat(employeeResponseDtos.getFirst()).isEqualTo(mapToEmployeeResponseDto(employee));
    }

    @Test
    @DisplayName("Find employees by last name should return matching Dtos")
    void findEmployees_byLastName_shouldReturnEmployeeResponseDtos() {
        Mockito.when(employeeRepository.findByLastNameIgnoreCase(employeeRequestDto.lastName())).thenReturn(List.of(employee));
        List<EmployeeResponseDto> employeeResponseDtos = employeeService.findByLastName(employeeRequestDto.lastName());
        Assertions.assertThat(employeeResponseDtos.size()).isEqualTo(1);
        Assertions.assertThat(employeeResponseDtos).containsExactly(mapToEmployeeResponseDto(employee));
        Assertions.assertThat(employeeResponseDtos.getLast()).isEqualTo(mapToEmployeeResponseDto(employee));
    }

    @Test
    @DisplayName("Find employees by department code should return matching DTOs")
    void findEmployees_byDepartmentCode_shouldReturnEmployeeResponseDtos() {
        Mockito.when(employeeRepository.findByDepartmentCodeIgnoreCase(employeeRequestDto.departmentCode())).thenReturn(List.of(employee));
        List<EmployeeResponseDto> employeeResponseDtos = employeeService.findByDepartmentCode(employeeRequestDto.departmentCode());
        Assertions.assertThat(employeeResponseDtos.size()).isEqualTo(1);
        Assertions.assertThat(employeeResponseDtos).containsExactly(mapToEmployeeResponseDto(employee));
        Assertions.assertThat(employeeResponseDtos.getFirst()).isEqualTo(mapToEmployeeResponseDto(employee));
    }


    @Test
    @DisplayName("Update employee when found should modify and return Dto")
    void updateEmployee_whenFound_shouldReturnEmployeeResponseDto() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);
        Optional<EmployeeResponseDto> employeeResponseDto = employeeService.updateEmployee(employeeRequestDto.email(), employeeRequestDto);
        Assertions.assertThat(employeeResponseDto).isPresent();
        Assertions.assertThat(employeeResponseDto.get()).isEqualTo(mapToEmployeeResponseDto(employee));
    }

    @Test
    @DisplayName("Update employee when found should modify and return Dto")
    void updatePartially_whenFound_shouldReturnEmployeeResponseDto() {
        Mockito.when(employeeRepository.findByEmail(employeeRequestDto.email())).thenReturn(Optional.of(employee));
        Mockito.when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        EmployeePatchDto employeePatchDto = new EmployeePatchDto("Bennett", "T", "Compro");
        Optional<EmployeeResponseDto> employeeResponseDto = employeeService.updateEmployeePartially(employeeRequestDto.email(), employeePatchDto);
        Assertions.assertThat(employeeResponseDto).isPresent();
        Assertions.assertThat(employeeResponseDto.get().firstName()).isEqualTo("Bennett");
        Assertions.assertThat(employeeResponseDto.get().lastName()).isEqualTo("T");
        Assertions.assertThat(employeeResponseDto.get().departmentCode()).isEqualTo("Compro");
    }

    private EmployeeResponseDto mapToEmployeeResponseDto(Employee employee) {
        return new EmployeeResponseDto(
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDepartmentCode()
        );
    }
}