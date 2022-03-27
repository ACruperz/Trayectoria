package com.svr.app.models;

public class AttendanceMdl {

    private Long id;
    private Long course;
    private Long userid;
    private Long section;

    private Float grademax;
    private Float grademin;
    private Float finalgrade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourse() {
        return course;
    }

    public void setCourse(Long course) {
        this.course = course;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Float getGrademax() {
        return grademax;
    }

    public void setGrademax(Float grademax) {
        this.grademax = grademax;
    }

    public Float getGrademin() {
        return grademin;
    }

    public void setGrademin(Float grademin) {
        this.grademin = grademin;
    }

    public Float getFinalgrade() {
        return finalgrade;
    }

    public void setFinalgrade(Float finalgrade) {
        this.finalgrade = finalgrade;
    }

    public Long getSection() {
        return section;
    }

    public void setSection(Long section) {
        this.section = section;
    }
}
