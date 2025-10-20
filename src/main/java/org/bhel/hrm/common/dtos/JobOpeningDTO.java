package org.bhel.hrm.common.dtos;

import java.io.Serializable;

public record JobOpeningDTO(
    int id,
    String title,
    String description,
    String department,
    JobStatus status
) implements Serializable {
    public enum JobStatus { OPEN, CLOSED, ON_HOLD }
}
