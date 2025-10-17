package org.bhel.hrm.common.models;

import java.io.Serial;
import java.io.Serializable;

public class Employee implements Serializable {
    // A version ID for serialization to prevent errors during updates.
    @Serial
    private static final long serialVersionUID = 1L;

    // TODO: Can add any necessary data members for Employee class.
    private String m_Id;
    private String m_FirstName;
    private String m_LastName;
    private String m_IdentificationCard;
    private String m_PassportNumber;



    Employee(String id, String firstName, String lastName) {
        m_Id = id;
        this.m_FirstName = firstName;
        this.m_LastName = lastName;
    }

    public String GetId() {
        return m_Id;
    }

    public void SetId(String id) {
        this.m_Id = id;
    }

    public String GetFirstName() {
        return m_FirstName;
    }

    public void SetFirstName(String firstName) {
        this.m_FirstName = firstName;
    }

    public String GetLastName() {
        return m_LastName;
    }

    public void SetLastName(String lastName) {
        this.m_LastName = lastName;
    }

    public String GetIdentificationCard() {
        return m_IdentificationCard;
    }

    public void SetIdentificationCard(String identificationCard) {
        this.m_IdentificationCard = identificationCard;
    }

    public String GetPassportNumber() {
        return m_PassportNumber;
    }

    public void SetPassportNumber(String passportNumber) {
        this.m_PassportNumber = passportNumber;
    }
}
