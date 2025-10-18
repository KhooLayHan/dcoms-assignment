package org.bhel.hrm.server.mapper;

import org.bhel.hrm.common.dtos.EmployeeDTO;
import org.bhel.hrm.server.domain.Employee;

import java.util.List;
import java.util.stream.Collectors;

public class EmployeeMapper {
    private EmployeeMapper() {
        throw new UnsupportedOperationException("EmployeeMapper class");
    }

    // Maps a single Domain object to a DTO
    public static EmployeeDTO toDto(Employee employee) {
        if (employee == null)
            return null;

        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setIcPassport(employee.getIcPassport());

        return dto;
    }

    // Maps a single DTO to a Domain object
    public static Employee toDomain(EmployeeDTO dto) {
        if (dto == null)
            return null;

        Employee employee = new Employee();

        employee.setId(dto.getId());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getFirstName());
        employee.setIcPassport(dto.getIcPassport());

        return employee;
    }

    // Converts a list of Domain objects to a list of DTOs
    public static List<EmployeeDTO> toDtoList(List<Employee> employees) {
        return employees.stream().map(EmployeeMapper::toDto).collect(Collectors.toList());
    }
}
