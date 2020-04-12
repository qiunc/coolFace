package com.example.myapplication.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class StudentAttendance extends LitePalSupport implements Serializable {
    private String date;

    private String subject;

    private String theClassName;

    private String studentNumber;

    private String StudentName;

    private String attendance;

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
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

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getAttendance() {
        return attendance;
    }
}
