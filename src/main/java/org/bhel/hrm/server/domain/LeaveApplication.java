package org.bhel.hrm.server.domain;

import org.bhel.hrm.common.dtos.LeaveApplicationDTO;
import org.bhel.hrm.server.daos.LeaveApplicationDAO;

import java.time.LocalDateTime;

public class LeaveApplication {
    private int id;
    private int employeeId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LeaveApplicationDTO.LeaveType type;
    private LeaveApplicationDTO.LeaveStatus status;
    private String reason;

    public LeaveApplication() {}

    public LeaveApplication(int id, int employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime, LeaveApplicationDTO.LeaveType type, LeaveApplicationDTO.LeaveStatus status, String reason) {
        this.id = id;
        this.employeeId = employeeId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.type = type;
        this.status = status;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LeaveApplicationDTO.LeaveType getType() {
        return type;
    }

    public void setType(LeaveApplicationDTO.LeaveType type) {
        this.type = type;
    }

    public LeaveApplicationDTO.LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveApplicationDTO.LeaveStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
