package com.example.myapplication.util;

import android.text.TextUtils;

import com.example.myapplication.db.StudentAttendance;
import com.example.myapplication.db.Subjects;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.List;


import okhttp3.Call;

public class DbUtil {

    public static void updateSubjectSQLite() {
        LitePal.deleteAll(Subjects.class);
        OkHttpUtils.get()
                .url(Constant.htUrl+"mustQueryAllSubject")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONArray AllSubject = new JSONArray(response);
                                for (int i = 0; i < AllSubject.length(); i++) {
                                    JSONObject classObject = AllSubject.getJSONObject(i);
                                    Subjects subject = new Subjects();
                                    List<Subjects> subjectsList = LitePal.where("subjectname = ?", classObject.getString("subjectname")).find(Subjects.class);
                                    if (subjectsList == null || subjectsList.size() == 0) {  //表中不存在的班级就创建，否则就跳过
                                        subject.setSubjectName(classObject.getString("subjectname"));
                                        subject.save();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public static void studentAttendance(StudentAttendance studentAttendance){

        OkHttpUtils.post()
                .url(Constant.htUrl+"mustAddStudentAttendance")
                .addParams("className",studentAttendance.getTheClassName())
                .addParams("Date",studentAttendance.getDate())
                .addParams("subjectName",studentAttendance.getSubject())
                .addParams("studentNumber",studentAttendance.getStudentNumber())
                .addParams("studentName",studentAttendance.getStudentName())
                .addParams("attendance",studentAttendance.getAttendance())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }
}
