package com.example.myapplication;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.myapplication.db.Class;
import com.example.myapplication.db.Student;
import com.example.myapplication.db.StudentAttendance;
import com.example.myapplication.db.Subjects;
import com.example.myapplication.util.Constant;
import com.example.myapplication.util.DbUtil;
import com.example.myapplication.util.ImageTools;
import com.example.myapplication.util.Utility;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * Created by qiunc on 2020/4/3 0003.
 */

public class FaceSearchActivity extends AppCompatActivity implements View.OnClickListener {
    private Uri imageUri;
    private String similarity;
    private ImageView iv_face;
    private Button b_face_choice;
    private Button b_face_enter;
    private Button b_class_choice;
    private Button b_subject_choice;
    private Button b_date_choice;
    private AlertDialog alertDialog;
    private String returnSubjectData;
    private ArrayList<String> stringArrayList;
    private String[] className;
    private static final int SCALE = 5;//照片缩小比例
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int ADD_SUBJECTS = 2;
    private static final String TAG = "FaceSearchFragment";
    private int flag1;
    private int flag2;

    private List<Student> studentList;
    private List<StudentAttendance> studentAttendanceList = new ArrayList<StudentAttendance>();;
    private StudentAttendance studentAttendance = new StudentAttendance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facesearch);
        initViews();
        initEvent();
    }


    private void initViews() {
        iv_face = (ImageView) findViewById(R.id.iv_face);
        b_face_choice = (Button) findViewById(R.id.b_face_choice);
        b_face_enter = (Button) findViewById(R.id.b_face_enter);
        b_date_choice = (Button) findViewById(R.id.b_date_choice);
        b_class_choice = (Button) findViewById(R.id.b_class_choice);
        b_subject_choice = (Button) findViewById(R.id.b_subject_choice);


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
                subjectChoice();
                break;
            default:
                break;
        }
    }

    private void detectFace() {
        flag2 = 0;
        showProgressDialog();
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
                Toast.makeText(FaceSearchActivity.this, "调用失败", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                if (response != null) {
                    JSONObject object;
                    try {
                        object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("faces");
                        flag1 = array.length();
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
                .addParams("api_secret", Constant.Secret)
                .addParams("face_token", face_token)
                .addParams("outer_id", Constant.outer_id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(FaceSearchActivity.this, "调用失败", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("results");
                    //比对结果置信度
                    //similarity=object.getString("confidence");
                    similarity = array.getJSONObject(0).getString("confidence");
                    String faceToken = array.getJSONObject(0).getString("face_token");

                    //用于参考的置信度阈值1e-3,1e-4,1e-5
                    //如果置信值低于“千分之一”阈值则不建议认为是同一个人；
                    // 如果置信值超过“十万分之一”阈值，则是同一个人的几率非常高。
                    JSONObject thresholds = object.getJSONObject("thresholds");

                    //1e-3
                    String yz_3 = thresholds.getString("1e-3");
                    if (Double.parseDouble(similarity) > Double.parseDouble(yz_3) && array != null) {
                        getditailByFaceToken(faceToken);
//                        search_face.setVisibility(View.VISIBLE);
//                        no_face.setVisibility(View.GONE);

                    } else {
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

    private void getditailByFaceToken(final String faceToken) {
        Log.d(TAG, "getditailByfaceToken");
        studentList = LitePal.where("facetoken = ? ", faceToken).find(Student.class);
        if (studentList.size() > 0) {
            for (Student student : studentList) {
                studentAttendance.setDate(b_date_choice.getText().toString());
                studentAttendance.setTheClassName(student.getTheClassName());
                studentAttendance.setStudentNumber(student.getStudentNumber());
                studentAttendance.setSubject(b_subject_choice.getText().toString());
                studentAttendance.setStudentName(student.getName());
                studentAttendance.setAttendance("出勤");
                studentAttendanceList.add(studentAttendance);
            }

            flag2++;
            if (flag2 == flag1){
                attendanceResult();
            }
        }
    }

    private void attendanceResult() {
        Log.d(TAG, "attendanceResult: ");
        OkHttpUtils.post()
                .url(Constant.htUrl+"mustQueryAllAttendance")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                Toast.makeText(FaceSearchActivity.this, "调用失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Utility.handleAttendanceResponse(response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        StudentAttendance studentAttendance1 = new StudentAttendance();
//                        studentAttendance1.setDate("2020年4月11日");
//                        studentAttendance1.setTheClassName("16光电2班");
//                        studentAttendance1.setStudentNumber("B20160404214");
//                        studentAttendance1.setStudentName("邱宁聪");
//                        studentAttendance1.setAttendance("出勤");

                        progressDialog.dismiss();

                        Bundle bundle = new Bundle();
                        Intent attendanceResultIntent = new Intent(FaceSearchActivity.this, AttendanceResultAcitivity.class);
                        bundle.putSerializable("attendanceResult",(Serializable)studentAttendanceList);
                        attendanceResultIntent.putExtras(bundle);
                        startActivity(attendanceResultIntent);
                    }
                });
            }
        });
    }

    private void dateChoice() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        final String data = (month + 1) + "月-" + dayOfMonth + "日 ";
                        b_date_choice.setText((String) (year + "年" + (month + 1) + "月" + dayOfMonth + "日"));
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
        for (Class classes : classList) {
            stringArrayList.add(classes.getTheClassName());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("选择一个班级");
        //    指定下拉列表的显示数据

        className = new String[stringArrayList.size()];

        className = stringArrayList.toArray(className);
        //    设置一个下拉的列表选择项
        builder.setItems(className, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                b_class_choice.setText(className[which]);
                Toast.makeText(FaceSearchActivity.this, "选择的班级为：" + className[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    private void subjectChoice() {
        stringArrayList = new ArrayList<>();
        List<Subjects> subjectsList = LitePal.findAll(Subjects.class);
        for (Subjects subjects : subjectsList) {
            stringArrayList.add(subjects.getSubjectName());
        }
//        if (returnSubjectData != null){
//            stringArrayList.add(returnSubjectData);
//        }
        stringArrayList.add("添加课程...");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setTitle("选择一个课程");
        //    指定下拉列表的显示数据

        className = new String[stringArrayList.size()];

        className = stringArrayList.toArray(className);
        //    设置一个下拉的列表选择项
        builder.setItems(className, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("添加课程...".equals(className[which])){
//                  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("输入增加的课程");
//                    builder.setIcon(R.mipmap.ic_launcher_round);
//                    builder.setView(new EditText(getActivity()));
//                    builder.setPositiveButton("添加",null);
//                    builder.setNegativeButton("取消",null);
                    Intent addSubjectIntent = new Intent(FaceSearchActivity.this, DialogActivity.class);
                    startActivityForResult(addSubjectIntent,ADD_SUBJECTS);
                }
                else {
                    b_subject_choice.setText(className[which]);
                    Toast.makeText(FaceSearchActivity.this, "选择的课程为：" + className[which], Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }


    private void choosePicture() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
                File outputImage = new File(FaceSearchActivity.this.getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(FaceSearchActivity.this, "com.example.cameraablumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
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
                        Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(imageUri));
                        iv_face.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = this.getContentResolver();
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
                case ADD_SUBJECTS:
                   String Data = data.getStringExtra("data_return");
                    addSubject(Data);
                    break;
                default:
                    break;
            }
        }
    }


    private void addSubject(String data){
        OkHttpUtils.post()
                .url(Constant.htUrl+"mustAddSubject")
                .addParams("subjectName",data)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(FaceSearchActivity.this, "连接数据库失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, response.toString());
                Toast.makeText(FaceSearchActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                DbUtil.updateSubjectSQLite();
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
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
