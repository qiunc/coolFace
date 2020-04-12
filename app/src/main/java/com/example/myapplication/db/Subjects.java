package com.example.myapplication.db;

import org.litepal.crud.LitePalSupport;

public class Subjects extends LitePalSupport {
    private int id;

    private String subjectName;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectName() {
        return subjectName;
    }
}
