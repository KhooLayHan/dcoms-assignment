package org.bhel.hrm.server.mapper;

import org.bhel.hrm.common.dtos.UserDTO;
import org.bhel.hrm.server.domain.User;

public final class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("This class UserMapper is a utility class; it should not be instantiated.");
    }

    public static UserDTO mapToDto(User user) {
        if (user == null)
            return null;

        return new UserDTO(
            user.getId(),
            user.getUsername(),
            user.getRole()
        );
    }

    public static User mapToDomain(UserDTO dto) {
        if (dto == null)
            return null;

        return new User(
            dto.id(),
            dto.username(),
            dto.role()
        );
    }
}
