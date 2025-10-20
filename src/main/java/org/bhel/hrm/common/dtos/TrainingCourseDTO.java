package org.bhel.hrm.common.dtos;

import java.io.Serializable;

public record TrainingCourseDTO(
    int id,
    String title,
    String description,
    int durationInHours,
    String department
) implements Serializable {}
