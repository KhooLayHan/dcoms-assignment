package org.bhel.hrm.server.mapper;

import org.bhel.hrm.common.dtos.LeaveApplicationDTO;
import org.bhel.hrm.server.domain.LeaveApplication;

public final class LeaveApplicationMapper {
    private LeaveApplicationMapper() {
        throw new UnsupportedOperationException("This class LeaveApplicationMapper is a utility class; it should not be instantiated.");
    }

    public static LeaveApplicationDTO mapToDto(LeaveApplication domain) {
        if (domain == null)
            return null;

        return new LeaveApplicationDTO(
            domain.getId(),
            domain.getEmployeeId(),
            domain.getStartDateTime(),
            domain.getEndDateTime(),
            domain.getType(),
            domain.getStatus(),
            domain.getReason()
        );
    }

    public static LeaveApplication mapToDomain(LeaveApplicationDTO dto) {
        if (dto == null)
            return null;

        return new LeaveApplication(
            dto.id(),
            dto.employeeId(),
            dto.startDateTime(),
            dto.endDateTime(),
            dto.type(),
            dto.status(),
            dto.reason()
        );
    }
}
