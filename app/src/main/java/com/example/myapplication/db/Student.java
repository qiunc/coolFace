package com.example.myapplication.db;

import org.litepal.crud.LitePalSupport;

public class Student extends LitePalSupport {

    private int id;

    private String theClassName;

    private String name;

    private String studentNumber;

    private String gender;

    private String facetoken;

    private String image;



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

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }


    public void setFacetoken(String facetoken) {
        this.facetoken = facetoken;
    }

    public String getFacetoken() {
        return facetoken;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }
}
