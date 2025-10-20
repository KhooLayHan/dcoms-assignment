package org.bhel.hrm.common.dtos;

import java.io.Serializable;

public record UserDTO(
    int id,
    String username,
    Role role
) implements Serializable {
    public enum Role { HR_STAFF, EMPLOYEE }
}
