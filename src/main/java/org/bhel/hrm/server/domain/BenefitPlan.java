package org.bhel.hrm.server.domain;

import java.math.BigDecimal;

public class BenefitPlan {
    private int id;
    private String planName;
    private String provider;
    private String description;
    private BigDecimal costPerMonth;

    public BenefitPlan() {}

    public BenefitPlan(int id, String planName, String provider, String description, BigDecimal costPerMonth) {
        this.id = id;
        this.planName = planName;
        this.provider = provider;
        this.description = description;
        this.costPerMonth = costPerMonth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getCostPerMonth() {
        return costPerMonth;
    }

    public void setCostPerMonth(BigDecimal costPerMonth) {
        this.costPerMonth = costPerMonth;
    }
}
