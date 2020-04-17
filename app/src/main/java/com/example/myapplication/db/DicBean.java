package com.example.myapplication.db;

/**
 * Created by import on 2018-01-31.
 */

public class DicBean {
    private String code;
    private String name;

    public DicBean(){
        this.code="";
        this.name="";
    }
    public DicBean(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public DicBean(String code, String name, String type) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
