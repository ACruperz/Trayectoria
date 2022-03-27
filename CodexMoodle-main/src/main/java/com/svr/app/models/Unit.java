package com.svr.app.models;

public class Unit {

    private int unit = 0;
    private int failedStudent = 0;
    private int dropoutStudents = 0;

    public Unit() {
    }

    public Unit(int unit, int failedStudent) {
        this.unit = unit;
        this.failedStudent = failedStudent;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getFailedStudent() {
        return failedStudent;
    }

    public void setFailedStudent(int failedStudent) {
        this.failedStudent = failedStudent;
    }

    public int getDropoutStudents() {
        return dropoutStudents;
    }

    public void setDropoutStudents(int dropoutStudents) {
        this.dropoutStudents = dropoutStudents;
    }
}
