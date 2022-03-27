package com.svr.app.models;


public class Grade {

    private Long course;
    private Long teacher;
    private String teacherFirstname;
    private String teacherLastname;
    private Long unit;
    private Long userid;
    private Long grade;

    public String getTeacherFirstname() {
        return teacherFirstname;
    }

    public void setTeacherFirstname(String teacherFirstname) {
        this.teacherFirstname = teacherFirstname;
    }

    public String getTeacherLastname() {
        return teacherLastname;
    }

    public void setTeacherLastname(String teacherLastname) {
        this.teacherLastname = teacherLastname;
    }

    public Long getCourse() {
        return course;
    }

    public void setCourse(Long course) {
        this.course = course;
    }

    public Long getTeacher() {
        return teacher;
    }

    public void setTeacher(Long teacher) {
        this.teacher = teacher;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

}
