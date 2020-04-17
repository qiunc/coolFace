package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
/**
 * Created by Qiunc on 2020/3/28 0003.
 * 添加课程所用到的dialog
 * 不用AlertDialog用DialogActivity
 */
public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        final EditText editText = (EditText) findViewById(R.id.et_return_subjectData);
        Button button = (Button) findViewById(R.id.b_return_subjectData);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String data = editText.getText().toString();
                intent.putExtra("data_return", data);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
