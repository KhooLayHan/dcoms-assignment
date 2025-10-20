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
    public static EmployeeDTO mapToDto(Employee employee) {
        if (employee == null)
            return null;

        return new EmployeeDTO(
            employee.getId(),
            employee.getUserId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getIcPassport()
        );
    }

    // Maps a single DTO to a Domain object
    public static Employee mapToDomain(EmployeeDTO dto) {
        if (dto == null)
            return null;

        return new Employee(
            dto.id(),
            dto.userId(),
            dto.firstName(),
            dto.lastName(),
            dto.icPassport()
        );
    }

    // Maps a list of Domain objects to a list of unmodifiable DTOs
    public static List<EmployeeDTO> mapToDtoList(List<Employee> employees) {
        return employees.stream().map(EmployeeMapper::mapToDto).toList();
    }
}
