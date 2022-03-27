package com.svr.app.models;

import java.util.*;

public class Subject {

    private Long id;
    private String clave;
    private String subject;
    private String teacher = "";
    private Long semester;
    private Long units;
    private int totalStudents = 0;
    private int activeStudent = 0;
    private int failingStudents = 0;
    private float failingRank = 0.0f;
    private int totalAttendance = 0;
    private int dropoutStudents = 0;
    private List<Unit> arrayUnits;
    private  List<Long> failureHistory = new ArrayList<>();
    private List<Long> dropoutHistory = new ArrayList<>();

    public Subject() {
        this.arrayUnits = new ArrayList<>();
    }


    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public List<Long> getFailureHistory() {
        return failureHistory;
    }

    public void setFailureHistory(List<Long> failureHistory) {
        this.failureHistory = failureHistory;
    }

    public void  addFailedHistory( List<Long> failureHistory) {
        if ( failureHistory != null ) {
            this.failureHistory.addAll( failureHistory );
        }
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
        this.failingRank = (failingRank);
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(int totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public int getDropoutStudents() {
        return dropoutStudents;
    }

    public void setDropoutStudents(int dropoutStudents) {
        this.dropoutStudents = dropoutStudents;
    }

    public List<Long> getDropoutHistory() {
        return dropoutHistory;
    }

    public void setDropoutHistory(List<Long> dropoutHistory) {
        this.dropoutHistory = dropoutHistory;
    }

    public void addDropoutHistory( Long iduser ) {
        this.dropoutHistory.add( iduser );
    }
    public void addAllDropoutHistory( List<Long> dropoutHistory ) {
        this.dropoutHistory.addAll( dropoutHistory );
    }

    public int getActiveStudent() {
        return activeStudent;
    }

    public void setActiveStudent(int activeStudent) {
        this.activeStudent = Math.max(activeStudent, 0);
    }


}
