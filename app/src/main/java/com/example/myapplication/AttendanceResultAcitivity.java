package com.example.myapplication;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Adapter.TableAdapter;
import com.example.myapplication.db.StudentAttendance;

import java.util.List;

public class AttendanceResultAcitivity extends AppCompatActivity {
    List<StudentAttendance> alist ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
       // tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));
        alist = (List<StudentAttendance>) getIntent().getSerializableExtra("attendanceResult");
        ListView tableListView = (ListView) findViewById(R.id.list);

        TableAdapter adapter = new TableAdapter(this, alist);

        tableListView.setAdapter(adapter);

    }
}
