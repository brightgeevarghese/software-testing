package com.bright.ems.controller;

import com.bright.ems.dto.request.EmployeePatchDto;
import com.bright.ems.dto.request.EmployeeRequestDto;
import com.bright.ems.dto.response.EmployeeResponseDto;
import com.bright.ems.exception.ApiError;
import com.bright.ems.exception.employee.DuplicateEmailException;
import com.bright.ems.exception.employee.EmployeeNotFoundException;
import com.bright.ems.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private final EmployeeRequestDto employeeRequestDto = new EmployeeRequestDto(
            "John",
            "Doe",
            "john@doe.com",
            "Compro"
    );
    private final EmployeeResponseDto employeeResponseDto = new EmployeeResponseDto(
            "John",
            "Doe",
            "Compro"
    );


    @Test
    @DisplayName("POST /employees should create and return employee")
    void createEmployee_shouldReturnEmployee() throws Exception {
        Mockito.when(employeeService.createEmployee(employeeRequestDto)).thenReturn(Optional.of(employeeResponseDto));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequestDto))
        )
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(employeeResponseDto)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.departmentCode").value("Compro"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("POST /employees with existing email should return Bad Request 400")
    void createEmployee_shouldReturnBadRequest_whenEmailAlreadyExists() throws Exception {
        Mockito.when(employeeService.createEmployee(employeeRequestDto)).thenThrow(new DuplicateEmailException("Employee already exists"));
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDto))
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @DisplayName("GET /employees should return a list of employees")
    void getEmployees_shouldReturnDtos() throws Exception {
        Mockito.when(employeeService.getAllEmployees()).thenReturn(List.of(employeeResponseDto));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/employees")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value("Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departmentCode").value("Compro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("PATCH /employees/{email} should update and return employee")
    void updatePartiallyEmployee_shouldReturnEmployee() throws Exception {
        var employeeRequestPatchDto = new EmployeePatchDto(
                "Jane",
                "McIntier",
                "Medicine"
        );
        var employeePatchResponseDto = new EmployeeResponseDto(
                "Jane",
                "McIntier",
                "Medicine"
        );
        Mockito.when(employeeService.updateEmployeePartially("john@doe.com", employeeRequestPatchDto)).thenReturn(Optional.of(employeePatchResponseDto));
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/employees/john@doe.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestPatchDto))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(employeePatchResponseDto)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Jane"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("McIntier"))
        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("PUT /employees/{email} should update and return employee")
    void updateEmployee_shouldReturnEmployee() throws Exception {
        Mockito.when(employeeService.updateEmployee("john@doe.com", employeeRequestDto)).thenReturn(Optional.of(employeeResponseDto));

        mockMvc.perform(
                MockMvcRequestBuilders.put("/api/v1/employees/john@doe.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDto))
        )
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(employeeResponseDto)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("John"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("DELETE /employees/{email} should delete and return 204")
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee("john@doe.com");
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/employees/john@doe.com")
        )
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("DELETE /employees/{email} non existing email should throw EmployeeNotFoundException")
    void deleteNonExistingEmployee_shouldThrowEmployeeNotFoundException() throws Exception {
        String email = "sam@gmail.com";
        String errorMessage = "Employee not found with email: " + email;
        Mockito.doThrow(new EmployeeNotFoundException(errorMessage)).when(employeeService).deleteEmployee(email);
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/employees/" + email)
        )
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andDo(MockMvcResultHandlers.print());
    }

}