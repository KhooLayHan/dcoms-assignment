package org.bhel.hrm.server.domain;

import org.bhel.hrm.common.dtos.JobOpeningDTO;

public class JobOpening {
    private int id;
    private String title;
    private String description;
    private String department;
    private JobOpeningDTO.JobStatus status;

    public JobOpening() {}

    public JobOpening(int id, String title, String description, String department, JobOpeningDTO.JobStatus status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.department = department;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public JobOpeningDTO.JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobOpeningDTO.JobStatus status) {
        this.status = status;
    }
}
