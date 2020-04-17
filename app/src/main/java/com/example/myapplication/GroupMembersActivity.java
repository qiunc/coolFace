package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.myapplication.Adapter.GroupMemberInformationAdapter;
import com.example.myapplication.db.Student;

import org.litepal.LitePal;

import java.util.List;


/**
 * Created by Qiunc on 2020/4/2 0003.
 * ChooseStudentFragment查看每个群组的成员信息时跳转的activity
 */

public class GroupMembersActivity extends AppCompatActivity {

    private SwipeMenuListView ListView;
    //定义一个列表集合
    private List<Student> studentList;
    private GroupMemberInformationAdapter groupMemberInformationAdapter;
    private static final int SCALE = 6;
    private String GroupName;
    private TextView titleText;
    private int po;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupmember);
        Intent intent = getIntent();
        GroupName = intent.getStringExtra("GroupName");
        titleText = (TextView) findViewById(R.id.tv_title_text);
        ListView = (SwipeMenuListView) findViewById(R.id.list__view);

        initData();
        initEvent();
    }


    public void initData() {
        //初始化groupmember数据
        titleText.setText(GroupName);
        studentList = LitePal.where("theClassName = ?", GroupName).order("studentNumber").find(Student.class);
    }

    /**
     * 初始化适配器需要的数据格式及listView
     */
    private void initEvent() {
        //初始化列表集合
        groupMemberInformationAdapter = new GroupMemberInformationAdapter(GroupMembersActivity.this, studentList);
        ListView.setAdapter(groupMemberInformationAdapter);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // 创建滑动选项
                SwipeMenuItem rejectItem = new SwipeMenuItem(
                        getApplicationContext());
                // 设置选项背景
                rejectItem.setBackground(new ColorDrawable(getResources().getColor(R.color.top)));
                // 设置选项宽度
                rejectItem.setWidth(dp2px(80, getApplicationContext()));
                // 设置选项标题
                rejectItem.setTitle("置顶");
                // 设置选项标题
                rejectItem.setTitleSize(16);
                // 设置选项标题颜色
                rejectItem.setTitleColor(Color.WHITE);
                // 添加选项
                menu.addMenuItem(rejectItem);

                // 创建删除选项
                SwipeMenuItem argeeItem = new SwipeMenuItem(getApplicationContext());
                argeeItem.setBackground(new ColorDrawable(getResources().getColor(R.color.del)));
                argeeItem.setWidth(dp2px(80, getApplicationContext()));
                argeeItem.setTitle("删除");
                argeeItem.setTitleSize(16);
                argeeItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(argeeItem);
            }
        };
        ListView.setMenuCreator(creator);
        ListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0: //第一个选项
                        Toast.makeText(GroupMembersActivity.this, "您点击的是置顶", Toast.LENGTH_SHORT).show();
                        break;
                    case 1: //第二个选项
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GroupMembersActivity.this);
                        dialog.setTitle("删除该成员信息");
                        dialog.setMessage("您确定要删除吗？");
                        dialog.setNegativeButton("取消",null);
                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                studentList.remove(position);
                                groupMemberInformationAdapter.notifyDataSetChanged();
                                if (studentList.size() <= 0){
                                    finish();
                                }
                            }
                        });
                        dialog.show();

                        break;
                }
                return false;
            }
        });
    }
    public int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}