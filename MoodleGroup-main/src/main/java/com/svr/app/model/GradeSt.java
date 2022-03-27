package com.svr.app.model;


import java.util.ArrayList;
import java.util.List;

public class GradeSt {


    private Integer idCourse;
    private String clave = "";
    private int failedUnits;

    private List<UnitSt> arrayUnits = new ArrayList<>();


    public Integer getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(Integer idCourse) {
        this.idCourse = idCourse;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public List<UnitSt> getArrayUnits() {
        return arrayUnits;
    }

    public void addUnits( List<UnitSt> arrayUnits ) {
        this.arrayUnits.addAll( arrayUnits );
    }

    public int getFailedUnits() {
        return failedUnits;
    }

    public void setFailedUnits(int failedUnits) {
        this.failedUnits = failedUnits;
    }
}
