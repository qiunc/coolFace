package com.example.myapplication.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.db.Student;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2018/5/3 0003.
 */

public class FaceAlbumFragment extends Fragment {

    private Button update_album;
    private ListView listView;
    //定义一个列表集合
    List<Map<String, Object>> listItems;
    List<Student> studentList;
    Map<String, Object> map;
    //定义一个simpleAdapter,供列表项使用
    SimpleAdapter simpleAdapter;
    private View view;
    private static final int SCALE = 6;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.facealbum, container, false);
        update_album = (Button)view.findViewById(R.id.update_album);
        initEvent();
        return view;
    }

    private void initEvent() {
        //初始化列表集合
        getFaceDetail();

        update_album.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                getFaceDetail();
            }
        });
    }

    public void getFaceDetail(){

       // final JSONObject json=new JSONObject();
        //后台接口

        studentList = LitePal.findAll(Student.class);
        listItems = new ArrayList<Map<String, Object>>();
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
                map.put("classname",student.getTheClassName());
                listItems.add(map);

            }
        Log.d("qnc", listItems.toString());
        loadAdapter();
   //     String url= Constant.htUrl+"QueryAllStudent";

//        OkHttpUtils
//                .get()
//                .url(url)
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        try {
//                            //Log.d("de", "onSuccess: "+s);
//                            JSONArray jsonArray = new JSONArray(response);
//                          //  json.put("result",jsonArray) ;
//                            Log.d("ht",jsonArray.toString());
//                            listItemsInit(jsonArray);  //将后台数据放入listView
//                            loadAdapter();  //加载适配器到listView
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

//        try {
//            return json.getJSONArray("result");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return null;
    }
    /**
         * 初始化适配器需要的数据格式
         */


    private void loadAdapter() {

        listView = (ListView) view.findViewById(R.id.list_view);
        // key值数组，适配器通过key值取value，与列表项组件一一对应
        String[] from=new String[]{"image","name","sex","studentNumber","classname"};
        // 列表项组件Id 数组
        int[] to=new int[]{R.id.iv_face, R.id.tv_name_v, R.id.tv_sex_v,
                R.id.tv_studentnumber_v, R.id.tv_classname_v};
        simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.face_item,
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

        listView.setAdapter(simpleAdapter);
    }


}