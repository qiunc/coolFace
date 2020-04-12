package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.myapplication.fragment.ChooseStudentFragment;
import com.example.myapplication.fragment.FaceAlbumFragment;
import com.example.myapplication.fragment.FaceCreatFragment;
import com.example.myapplication.fragment.SearchAttendanceResultFragment;

public class MainActivity extends AppCompatActivity {

    private RadioGroup mTabRadioGroup;
    private SparseArray<Fragment> mFragmentSparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTabRadioGroup = findViewById(R.id.tabs_rg);
        mFragmentSparseArray = new SparseArray<>();
        mFragmentSparseArray.append(R.id.today_tab, new ChooseStudentFragment());
        mFragmentSparseArray.append(R.id.record_tab, new FaceCreatFragment());
        mFragmentSparseArray.append(R.id.contact_tab, new FaceAlbumFragment());
        mFragmentSparseArray.append(R.id.settings_tab,new SearchAttendanceResultFragment());
        mTabRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 具体的fragment切换逻辑可以根据应用调整，例如使用show()/hide()
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        mFragmentSparseArray.get(checkedId)).commit();
            }
        });
        // 默认显示第一个
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                mFragmentSparseArray.get(R.id.today_tab)).commit();
        findViewById(R.id.sign_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FaceSearchActivity.class);
                startActivity(intent);
            }
        });
    }

}
