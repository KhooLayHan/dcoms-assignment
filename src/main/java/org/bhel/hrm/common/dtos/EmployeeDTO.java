package org.bhel.hrm.common.dtos;

import java.io.Serializable;

public record EmployeeDTO(
    int id,
    int userId,
    String firstName,
    String lastName,
    String icPassport
) implements Serializable {}
