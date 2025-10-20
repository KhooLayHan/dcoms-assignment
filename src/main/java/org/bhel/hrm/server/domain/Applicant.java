package org.bhel.hrm.server.domain;

import org.bhel.hrm.common.dtos.ApplicantDTO;

public class Applicant {
    private int id;
    private int jobOpeningId;
    private String fullName;
    private String email;
    private String phone;
    private ApplicantDTO.ApplicantStatus status;

    public Applicant() {}

    public Applicant(int id, int jobOpeningId, String fullName, String email, String phone, ApplicantDTO.ApplicantStatus status) {
        this.id = id;
        this.jobOpeningId = jobOpeningId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobOpeningId() {
        return jobOpeningId;
    }

    public void setJobOpeningId(int jobOpeningId) {
        this.jobOpeningId = jobOpeningId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ApplicantDTO.ApplicantStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicantDTO.ApplicantStatus status) {
        this.status = status;
    }
}
