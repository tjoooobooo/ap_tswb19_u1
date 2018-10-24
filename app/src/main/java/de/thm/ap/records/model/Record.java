package de.thm.ap.records.model;

import java.io.Serializable;

public class Record implements Serializable {
    Integer id, crp, mark, year;
    String moduleNum, moduleName;
    Boolean summerTerm;
    Boolean halfWeighted;


    public Record(String moduleNum, String moduleName, Integer year, Boolean summerTerm, Boolean halfWeighted, Integer crp, Integer mark){
        id = null;
        this.crp = crp;
        this.mark = mark;
        this.moduleNum = moduleNum;
        this.moduleName = moduleName;
        this.year = year;
        this.summerTerm = summerTerm;
        this.halfWeighted = halfWeighted;
    }

    public Record(){
        id = null;
        crp = null;
        mark = null;
        moduleName = null;
        moduleName = null;
        year = null;
        summerTerm = null;
        halfWeighted = null;
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

    @Override
    public String toString(){
        return moduleName + " " + moduleNum + " (" + mark + "% " + crp + "crp" +")";
    }
}
