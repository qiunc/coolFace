package com.example.myapplication.fragment;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.db.Class;
import com.example.myapplication.db.Student;
import com.example.myapplication.util.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;

/**
 * Created by qiunc on 2020/4/3 0003.
 */

public class FaceSearchFragment extends Fragment implements View.OnClickListener {
    private Uri imageUri;
    private String similarity;
    private ImageView iv_face;
    private Button b_face_choice;
    private Button b_face_enter;
    private Button b_class_choice;
    private Button b_subject_choice;
    private Button b_date_choice;
    private AlertDialog alertDialog;

    private ArrayList<String> stringArrayList;
    private String[] className;
  //  private TextView tv_similar;
//    private ImageView iv_result_face;
  //  private TextView tv_name_v;
  //  private TextView tv_sex_v;
  //  private TextView tv_age_v;
  //  private TextView tv_yz_v;
    private static final int SCALE = 5;//照片缩小比例
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final String TAG = "FaceSearchFragment";
    private View view;
//    private View search_face;
//    private View no_face;


    private List<Student> studentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.facesearch, container, false);
        initViews();
        initEvent();
        return view;
    }

    private void initViews() {
        iv_face = (ImageView) view.findViewById(R.id.iv_face);
        b_face_choice = (Button) view.findViewById(R.id.b_face_choice);
        b_face_enter = (Button) view.findViewById(R.id.b_face_enter);
        b_date_choice = (Button) view.findViewById(R.id.b_date_choice);
        b_class_choice = (Button) view.findViewById(R.id.b_class_choice);
        b_subject_choice = (Button) view.findViewById(R.id.b_subject_choice);


//        search_face = (View) view.findViewById(R.id.search_face);
//        no_face = (View) view.findViewById(R.id.no_face);

    }



    private void initEvent() {
        b_face_choice.setOnClickListener(this);
        b_face_enter.setOnClickListener(this);
        b_subject_choice.setOnClickListener(this);
        b_class_choice.setOnClickListener(this);
        b_date_choice.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_face_choice:
                   choosePicture();
                break;
            case R.id.b_face_enter:
                detectFace();
                break;
            case R.id.b_date_choice:
                dateChoice();
                break;
            case R.id.b_class_choice:
                classChoice();
                break;
            case R.id.b_subject_choice:

                break;
            default:
                break;
        }
    }

    private void detectFace() {
        //将图片转化为bitmap
        Bitmap bitmap = ((BitmapDrawable) iv_face.getDrawable()).getBitmap();
        //这里api要求传入一个字节数组数据，因此要用字节数组输出流
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
        byte[] image = oStream.toByteArray();
        String image_base64 = Constant.encode(image);

        //调用search API发起网络请求
        OkHttpUtils.post()
                .url(Constant.detectUrl)
                .addParams("api_key", Constant.Key)
                .addParams("api_secret", Constant.Secret)
                .addParams("image_base64", image_base64)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.d(TAG, e.toString());
                Toast.makeText(getActivity(), "调用失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                if (response != null) {
                    JSONObject object;
                    try {
                        object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("faces");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject oj = (JSONObject) array.get(i);
                            String face_token = (String) oj.get("face_token");
                            searchFace(face_token);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void searchFace(String face_token) {
        OkHttpUtils.post()
                .url(Constant.searchUrl)
                .addParams("api_key", Constant.Key)
                .addParams("api_secret",Constant.Secret)
                .addParams("face_token", face_token)
                .addParams("outer_id", Constant.outer_id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(),"调用失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d("search",response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("results");
                    //比对结果置信度
                    //similarity=object.getString("confidence");
                    similarity=array.getJSONObject(0).getString("confidence");
                    String faceToken = array.getJSONObject(0).getString("face_token");

                    //用于参考的置信度阈值1e-3,1e-4,1e-5
                    //如果置信值低于“千分之一”阈值则不建议认为是同一个人；
                    // 如果置信值超过“十万分之一”阈值，则是同一个人的几率非常高。
                    JSONObject thresholds = object.getJSONObject("thresholds");

                    //1e-3
                    String yz_3=thresholds.getString("1e-3");
                    if(Double.parseDouble(similarity)>Double.parseDouble(yz_3) && array != null){
                        getditailByFaceToken(faceToken);
//                        search_face.setVisibility(View.VISIBLE);
//                        no_face.setVisibility(View.GONE);

                    }else{
//                        no_face.setVisibility(View.VISIBLE);
//                        search_face.setVisibility(View.GONE);
                    }
                    //JSONArray array = object.getJSONArray("results");
                    //getditailByFaceToken(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getditailByFaceToken(final String faceToken){
        studentList = LitePal.where("facetoken = ? ",faceToken).find(Student.class);
        for (Student student : studentList) {
            Log.d(TAG, student.getTheClassName());
            Log.d(TAG, student.getStudentNumber());
            Log.d(TAG, student.getName());
        }

    }

    private void dateChoice(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final String data =  (month+1) + "月-" + dayOfMonth + "日 ";
                        b_date_choice.setText((String)(year+"年"+(month+1)+"月"+dayOfMonth+"日"));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    private void classChoice() {
        stringArrayList = new ArrayList<String>();
        List<Class> classList = LitePal.findAll(Class.class);
        for (Class classes : classList){
            stringArrayList.add(classes.getTheClassName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("选择一个城市");
        //    指定下拉列表的显示数据

        className = new String[stringArrayList.size()];

        className = stringArrayList.toArray(className);
        //    设置一个下拉的列表选择项
        builder.setItems(className, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                b_class_choice.setText(className[which]);
                Toast.makeText(getActivity(), "选择的城市为：" + className[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    private void subjectChoice() {

    }



    private void choosePicture() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("图片来源");
        dialog.setNegativeButton("取消", null);
        dialog.setItems(new String[]{"拍照", "相册"}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        takePhoto();
                        break;
                    case CHOOSE_PICTURE:
                        chooseAlbum();
                        break;
                    default:
                        break;
                }
            }

            private void chooseAlbum() {
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
            }

            private void takePhoto() {
                File outputImage=new File(getActivity().getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(getActivity(),"com.example.cameraablumtest.fileprovider",outputImage);
                }else{
                    imageUri=Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        dialog.create().show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Fragment selFragment = new Fragment();
//        selFragment.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    try {
                        //将拍摄的照片显示出来
                        Bitmap bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                        iv_face.setImageBitmap(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getActivity().getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            iv_face.setImageBitmap(smallBitmap);
                            //iv_face.setImageBitmap(photo);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


}
