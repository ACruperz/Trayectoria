package com.svr.app.model;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private Long id;
    private String group;

    private List<Subject> arraySubjects = new ArrayList<>();
    private List<Student> arrayStudents = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Student> getArrayStudents() {
        return arrayStudents;
    }

    public void addArrayStudents( Student student ) {
        this.arrayStudents.add( student );
    }

    public List<Subject> getArraySubjects() {
        return arraySubjects;
    }

    public void addArraySubjects(List<Subject> arraySubjects) {
        this.arraySubjects.addAll( arraySubjects );
    }
}
