package org.bhel.hrm.common.dtos;

import java.io.Serializable;
import java.time.LocalDateTime;

public record LeaveApplicationDTO(
    int id,
    int employeeId,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    LeaveType type,
    LeaveStatus status,
    String reason
) implements Serializable {
    public enum LeaveStatus { PENDING, APPROVED, REJECTED }
    public enum LeaveType { ANNUAL, SICK, UNPAID }
}

