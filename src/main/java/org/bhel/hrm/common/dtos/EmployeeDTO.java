package org.bhel.hrm.common.dtos;

import java.io.Serial;
import java.io.Serializable;

public class EmployeeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String firstName;
    private String lastName;
    private String icPassport;

    public EmployeeDTO() {}

    public String getIcPassport() {
        return icPassport;
    }

    public void setIcPassport(String icPassport) {
        this.icPassport = icPassport;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
