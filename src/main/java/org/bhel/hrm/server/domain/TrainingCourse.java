package org.bhel.hrm.server.domain;

public class TrainingCourse {
    private int id;
    private String title;
    private String description;
    private int durationInHours;
    private String department;

    public TrainingCourse() {}

    public TrainingCourse(int id, String title, String description, int durationInHours, String department) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationInHours = durationInHours;
        this.department = department;
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

    public int getDurationInHours() {
        return durationInHours;
    }

    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
