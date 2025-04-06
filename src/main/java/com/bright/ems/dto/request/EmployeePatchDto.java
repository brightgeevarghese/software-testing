package com.bright.ems.dto.request;

public record EmployeePatchDto(
        String firstName,
        String lastName,
        String departmentCode
) {
}
