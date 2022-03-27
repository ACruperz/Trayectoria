package com.svr.app.model;

import java.util.ArrayList;
import java.util.List;

public class Student {

    private Long userid;

    private String studentFirstname;
    private String studentLastname;
    private int totalFailedUnits;
    private List<GradeSt> arraySubject = new ArrayList<>();

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getStudentFirstname() {
        return studentFirstname;
    }

    public void setStudentFirstname(String studentFirstname) {
        this.studentFirstname = studentFirstname;
    }

    public String getStudentLastname() {
        return studentLastname;
    }

    public void setStudentLastname(String studentLastname) {
        this.studentLastname = studentLastname;
    }

    public List<GradeSt> getArraySubjectSt() {
        return arraySubject;
    }

    public void addArraySubjectSt(List<GradeSt> arraySubjects) {
        this.arraySubject.addAll( arraySubjects );
    }

    public int getTotalFailedUnits() {
        return totalFailedUnits;
    }

    public void setTotalFailedUnits(int totalFailedUnits) {
        this.totalFailedUnits = totalFailedUnits;
    }
}
