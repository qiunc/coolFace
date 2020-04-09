package com.example.myapplication.db;

import org.litepal.crud.LitePalSupport;

public class Class extends LitePalSupport {

    private int id;

    private String theClassName;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTheClassName(String theClassName) {
        this.theClassName = theClassName;
    }

    public String getTheClassName() {
        return theClassName;
    }
}
