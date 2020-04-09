package com.example.myapplication.util;

import android.text.TextUtils;
import com.example.myapplication.db.Class;
import com.example.myapplication.db.Student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;

;

public class Utility {

    /**
     * 解析和处理服务器返回数据
     */
    public static boolean handleClassResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allClass = new JSONArray(response);
                for (int i = 0; i < allClass.length(); i++) {
                    JSONObject classObject = allClass.getJSONObject(i);
                    Class aClass = new Class();
                    List<Class> classes = LitePal.where("theClassName = ?", classObject.getString("classname")).find(Class.class);
                    if(classes == null || classes.size() == 0) {  //表中不存在的班级就创建，否则就跳过
                        aClass.setTheClassName(classObject.getString("classname"));
                        aClass.save();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                JSONArray allStudent = new JSONArray(response);
                for (int i = 0; i < allStudent.length(); i++) {
                    JSONObject studentObject = allStudent.getJSONObject(i);
                    Student student = new Student();
                    student.setId(studentObject.getInt("id"));
                    student.setTheClassName(studentObject.getString("classname"));
                    student.setStudentNumber(studentObject.getString("studentnumber"));
                    student.setName(studentObject.getString("name"));
                    student.setGender(studentObject.getString("gender"));
                    student.setFacetoken(studentObject.getString("facetoken"));
                    student.setImage(studentObject.getString("image"));
                    student.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



//    /**
//     * 解析和处理服务器返回的市级数据
//     */
//    public static boolean handleCityResponse(String response, int provinceId) {
//        if (!TextUtils.isEmpty(response)) {
//            try {
//                JSONArray allCities = new JSONArray(response);
//                for (int i = 0; i < allCities.length(); i++) {
//                    JSONObject cityObject = allCities.getJSONObject(i);
//                    City city = new City();
//                    city.setCityName(cityObject.getString("name"));
//                    city.setCityCode(cityObject.getInt("id"));
//                    city.setProvinceId(provinceId);
//                    city.save();
//                }
//                return true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }
//
//    /**
//     * 解析和处理服务器返回的县级数据
//     */
//    public static boolean handleCountyResponse(String response, int cityId) {
//        if (!TextUtils.isEmpty(response)) {
//            try {
//                JSONArray allCounties = new JSONArray(response);
//                for (int i = 0; i < allCounties.length(); i++) {
//                    JSONObject countyObject = allCounties.getJSONObject(i);
//                    County county = new County();
//                    county.setCountyName(countyObject.getString("name"));
//                    county.setWeatherId(countyObject.getString("weather_id"));
//                    county.setCityId(cityId);
//                    county.save();
//                }
//                return true;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

}
