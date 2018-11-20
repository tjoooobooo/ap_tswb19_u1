package de.thm.ap.leistungen.model;

import java.io.Serializable;

public class Module implements Serializable {

    private String nr, name;
    private Integer crp;
    Integer id;


    public Module(String nr, String name, Integer crp) {
        this.nr = nr;
        this.name = name;
        this.crp = crp;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCrp() {
        return crp;
    }

    public void setCrp(Integer crp) {
        this.crp = crp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
