package de.thm.ap.leistungen.model;

import java.io.Serializable;

public class Record implements Serializable {
    private Integer id, crp, mark, year;
    private String moduleNum, moduleName;
    private Boolean summerTerm, halfWeighted, hasPassed, hasMark;

    public Record(String moduleNum, String moduleName, Integer year, Boolean summerTerm, Boolean halfWeighted, Integer crp, Integer mark){
        id = null;
        this.crp = crp;
        this.mark = mark;
        this.moduleNum = moduleNum;
        this.moduleName = moduleName;
        this.year = year;
        this.summerTerm = summerTerm;
        this.halfWeighted = halfWeighted;
        hasPassed = true;
        hasMark = true;
    }

    public Record(){
        id = null;
        crp = null;
        mark = null;
        moduleName = null;
        moduleNum = null;
        year = null;
        summerTerm = false;
        halfWeighted = false;
        hasPassed = true;
        hasMark = true;
    }

    public String getModuleNum(){
        return moduleNum;
    }

    public Integer getId() {
        return id;
    }

    public Integer getYear() {
        return year;
    }

    public Boolean isSummerTerm() {
        return summerTerm;
    }

    public Boolean isHalfWeighted() {
        return halfWeighted;
    }

    public Integer getCrp() {
        return crp;
    }

    public Integer getMark() {
        return mark;
    }

    public String getModuleName() {return moduleName;}

    public void setId(Integer id) {
        this.id = id;
    }

    public void setModuleNum(String moduleNum) {
        this.moduleNum = moduleNum;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setSummerTerm(Boolean summerTerm) {
        this.summerTerm = summerTerm;
    }

    public void setHalfWeighted(Boolean halfWeighted) {
        this.halfWeighted = halfWeighted;
    }

    public void setCrp(Integer crp) {
        this.crp = crp;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public void setModuleName(String moduleName) {this.moduleName = moduleName; }

    public void setNotPassed(){ hasPassed = false; }

    public boolean passed(){ return hasPassed; }

    public void setNoMark(){ hasMark = false; }

    public boolean hasMark() { return hasMark;}

    @Override
    public String toString(){
        String output = moduleName + " " + moduleNum + " (";
        if(hasMark()) output += mark + "% ";
        return output + crp + "crp" +")";
    }
}
