package com.svr.app.models;

import java.util.ArrayList;
import java.util.List;

public class SubjectMdl {

    private String subject;
    private String teacher = "";
    private Long semester;
    private Long units;
    private int totalStudents;
    private int failingStudents;
    private int activeStudents;
    private float failingRank;
    private int dropoutStudents = 0;
    private List<Unit> arrayUnits;


    public SubjectMdl() {
        this.arrayUnits = new ArrayList<>();
    }


    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getFailingStudents() {
        return failingStudents;
    }

    public void setFailingStudents(int failingStudents) {
        this.failingStudents = failingStudents;
    }

    public float getFailingRank() {
        return failingRank;
    }

    public void setFailingRank(float failingRank) {
        this.failingRank = failingRank;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public List<Unit> getArrayUnits() {
        return arrayUnits;
    }

    public void setArrayUnits(List<Unit> arrayUnits) {
        this.arrayUnits = arrayUnits;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getSemester() {
        return semester;
    }

    public void setSemester(Long semester) {
        this.semester = semester;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }

    public int getDropoutStudents() {
        return dropoutStudents;
    }

    public void setDropoutStudents(int dropoutStudents) {
        this.dropoutStudents = dropoutStudents;
    }

    public int getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(int activeStudents) {
        this.activeStudents = activeStudents;
    }
}
