package com.svr.app.model;

public class UnitSt {

    private Long unit;
    private Long grade;
    private int failedUnit;

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public int getFailedUnit() {
        return failedUnit;
    }

    public void setFailedUnit(int failedUnit) {
        this.failedUnit = failedUnit;
    }
}
