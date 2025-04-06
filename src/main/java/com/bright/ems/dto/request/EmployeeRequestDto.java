package com.bright.ems.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmployeeRequestDto(
        @NotBlank(message = "Null/empty are not acceptable")
        String firstName,
        @NotBlank(message = "Null/empty are not acceptable")
        String lastName,
        @NotBlank(message = "Null/empty are not acceptable")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Null/empty are not acceptable")
        String departmentCode
) {
}
