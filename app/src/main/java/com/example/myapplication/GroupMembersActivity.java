package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.db.Student;
import com.example.myapplication.util.ImageTools;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Qiunc on 2020/4/2 0003.
 * ChooseStudentFragment查看每个群组的成员信息时跳转的activity
 */

public class GroupMembersActivity extends AppCompatActivity {

    private ListView listView;
    //定义一个列表集合
    private List<Map<String, Object>> listItems;
    private List<Student> studentList;
    private Map<String, Object> map;
    //定义一个simpleAdapter,供列表项使用
    private SimpleAdapter simpleAdapter;
    private static final int SCALE = 6;
    private String GroupName;
    private TextView titleText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupmember);
        Intent intent = getIntent();
        GroupName = intent.getStringExtra("GroupName");
        titleText = (TextView)findViewById(R.id.tv_title_text);
        initEvent();
    }

    private void initEvent() {
        //初始化列表集合
        getFaceDetail();
    }

    public void getFaceDetail(){
        titleText.setText(GroupName);
        studentList = LitePal.where("theClassName = ?", GroupName).order("studentNumber").find(Student.class);
        listItems = new ArrayList<Map<String, Object>>();
        if(studentList.size() > 0) {
            for (Student student : studentList){
//                Log.d("qnc", student.getStudentNumber());
//                Log.d("qnc", student.getGender());
//                Log.d("qnc", student.getName());
//                Log.d("qnc", student.getTheClassName());
                map = new HashMap<String, Object>();
                map.put("image",student.getImage());
                map.put("name",student.getName());
                map.put("sex",student.getGender());
                map.put("studentNumber",student.getStudentNumber());
                listItems.add(map);
            }
            loadAdapter();
        }
        else {
            Toast.makeText(this,"暂无成员信息，请注册成员",Toast.LENGTH_SHORT).show();
        }

    }
    /**
         * 初始化适配器需要的数据格式
         */


    private void loadAdapter() {

        listView = (ListView)findViewById(R.id.list_view);
        // key值数组，适配器通过key值取value，与列表项组件一一对应
        String[] from=new String[]{"image","name","sex","studentNumber"};
        // 列表项组件Id 数组
        int[] to=new int[]{R.id.iv_face, R.id.tv_name_v, R.id.tv_sex_v,
                R.id.tv_studentnumber_v};
        simpleAdapter = new SimpleAdapter(this, listItems, R.layout.face_item,
                from, to);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if((view instanceof ImageView) && (o instanceof String)) {
                    ImageView vi = (ImageView) view;
                    String url = (String) o;
                    //  byte[] bytes = new BASE64Decoder().decodeBuffer(url);
                    Bitmap bm = BitmapFactory.decodeFile(url);
                    if (bm != null) {
                        //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        Bitmap smallBitmap = ImageTools.zoomBitmap(bm, bm.getWidth() / SCALE, bm.getHeight() / SCALE);
                        //释放原始图片占用的内存，防止out of memory异常发生
                        bm.recycle();
                        vi.setImageBitmap(smallBitmap);
                    }


                    return true;
                }
                return false;
            }
        });
        if (listItems.size() > 0) {
            listView.setAdapter(simpleAdapter);
        }
        else {
            Toast.makeText(this,"暂无成员信息，请注册成员",Toast.LENGTH_SHORT).show();
        }
    }
}