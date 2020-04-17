package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.GroupMembersActivity;
import com.example.myapplication.R;
import com.example.myapplication.db.Class;
import com.example.myapplication.db.Student;
import com.example.myapplication.util.Constant;
import com.example.myapplication.util.DbUtil;
import com.example.myapplication.util.HttpUtil;
import com.example.myapplication.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by Qiunc on 2020/3/28 0003.
 */

public class GroupFragment extends Fragment {
    public static final int LEVEL_CLASS = 0;

    public static final int LEVEL_STUDENT = 1;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Class> classList;

    private List<Student> studentList;

    private Class selectedClass;

    private int currentLevel;

    private SwipeRefreshLayout swipeRefresh;



    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_group, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        swipeRefresh = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String address = Constant.htUrl + "QueryAllStudent";
                DbUtil.updateSubjectSQLite();
                DbUtil.updateStudentAttendance();
                queryFromServer(address, "yourClass");
            }
        });
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_CLASS){
                    selectedClass =classList.get(position);
                    Intent intent = new Intent(getActivity(), GroupMembersActivity.class);
                    intent.putExtra("GroupName",selectedClass.getTheClassName());
                    startActivity(intent);
                }
            }
        });
//
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(currentLevel == LEVEL_STUDENT) {
//                    queryClass();
//                }
//            }
//        });
        queryClass();
    }


    private void queryClass(){
        titleText.setText("CCSU");
        classList = LitePal.findAll(Class.class);
        if (classList.size() > 0) {
            dataList.clear();
            for (Class classes : classList) {
                dataList.add(classes.getTheClassName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CLASS;
        }
    }

//    private void queryStudent() {
//        titleText.setText(selectedClass.getTheClassName());
//        backButton.setVisibility(View.VISIBLE);
//        studentList = LitePal.where("theClassName = ?", selectedClass.getTheClassName()).find(Student.class);
//        if (studentList.size() > 0) {
//            dataList.clear();
//            for (Student student : studentList) {
//                dataList.add(student.getStudentNumber()+"          "+student.getGender()+"          "+student.getName());
//            }
//            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
//            currentLevel = LEVEL_STUDENT;
//        }
//    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();

        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("yourClass".equals(type)) {
                    result = Utility.handleClassResponse(responseText);
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("yourClass".equals(type)) {
                                queryClass();
                                adapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            }
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {

            progressDialog.dismiss();
        }
    }

}
