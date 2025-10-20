package org.bhel.hrm.common.dtos;

import java.io.Serializable;

/**
 * A specialized DTO to carry all necessary information for registering a new employee,
 * which includes creating both a User account and an Employee profile.
 */
public record NewEmployeeRegistrationDTO(
    // User account details
    String username,
    String initialPassword,
    UserDTO.Role role,

    // Employee profile details
    String firstName,
    String lastName,
    String icPassport
) implements Serializable {}
