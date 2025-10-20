package org.bhel.hrm.server.domain;

import org.bhel.hrm.common.dtos.UserDTO;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private UserDTO.Role role;

    public User() {}

    public User(int id, String username, UserDTO.Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public User(int id, String username, String passwordHash, UserDTO.Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public UserDTO.Role getRole() {
        return role;
    }

    public void setRole(UserDTO.Role role) {
        this.role = role;
    }
}
