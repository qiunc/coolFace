package com.example.myapplication.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.util.Constant;
import com.heynchy.compress.compressUtil.FileSizeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Qiunc on 2020/3/28 0003.
 */

public class FaceCreatFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FaceCreatFragment";

    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private ImageView iv_face;
    private TextView result;
    private Button b_face_add;
    private Button b_face_choice;
    private EditText e_name;
    private EditText e_className;
    private EditText e_studentNumber;
    private Uri imageUri;
    private View view;
    private String imageAbsolutePath;
    private static final int SCALE = 5;//照片缩小比例
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.facecreate, container, false);
        initViews();
        initEvent();
        // getDetail();
        return view;
    }

    private void initViews() {
        iv_face = (ImageView) view.findViewById(R.id.iv_face);
        b_face_choice = (Button) view.findViewById(R.id.bt_login_face_choise);
        b_face_add = (Button) view.findViewById(R.id.bt_register);
        e_name = (EditText) view.findViewById(R.id.et_login_username);
        e_className = (EditText) view.findViewById(R.id.et_register_classname);
        e_studentNumber = (EditText) view.findViewById(R.id.et_register_studentnumher);

        //result=(TextView)view.findViewById(R.id.result);
    }

    private void initEvent() {
        b_face_choice.setOnClickListener(this);
        b_face_add.setOnClickListener(this);
    }

    @Override

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.bt_login_face_choise:
                choosePicture();
                break;
            case R.id.bt_register:
                detectFace();
                break;
        }
    }


    private void detectFace() {
        showProgressDialog();
        //将图片转化为bitmap
        final Bitmap bitmap = ((BitmapDrawable) iv_face.getDrawable()).getBitmap();
        //这里api要求传入一个字节数组数据，因此要用字节数组输出流
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
                /*Bitmap.compress()方法可以用于将Bitmap-->byte[]
                      既将位图的压缩到指定的OutputStream。如果返回true，
                      位图可以通过传递一个相应的InputStream BitmapFactory.decodeStream（重建）
                      第一个参数可设置JPEG或PNG格式,第二个参数是图片质量，第三个参数是一个流信息*/
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
        final byte[] image = oStream.toByteArray();
        final String image_base64 = Constant.encode(image);//转码后人脸照片对应的编码格式
        try {
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OkHttpUtils.post()
                .url(Constant.detectUrl)
                .addParams("api_key", Constant.Key)
                .addParams("api_secret", Constant.Secret)
                .addParams("image_base64", image_base64)
                .addParams("return_attributes", "gender,age,beauty")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "调用失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, response.toString());
                if (response != null) {
                    JSONObject object;
                    try {
                        object = new JSONObject(response);
                        JSONArray array = object.getJSONArray("faces");
                        String face_token;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject oj = (JSONObject) array.get(i);
                            face_token = oj.getString("face_token");
                            if (face_token != null) {
                                //将face_token发送到人脸集合
                                Log.d(TAG, face_token);
                                sendFaceToken(face_token);
                                //将人脸信息发送到数据库保存
                                sendFaceInfo(oj, imageAbsolutePath, face_token);

                                // getDetail();
                            } else {
                                Toast.makeText(getActivity(), "上传图片无法获取人脸标识", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendFaceInfo(JSONObject jsonObject, String image, String faceToken) {
        try {
            JSONObject attr = jsonObject.getJSONObject("attributes");
            String gender = attr.getJSONObject("gender").getString("value");
            OkHttpUtils.post()
                    .url("http://192.168.33.102:8080/AndroidTest/AddStudent")
                    .addParams("className", String.valueOf(e_className.getText()))
                    .addParams("studentNumber", String.valueOf(e_studentNumber.getText()))
                    .addParams("name", String.valueOf(e_name.getText()))
                    .addParams("gender", gender)
                    .addParams("image", image)
                    .addParams("faceToken", faceToken)
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(getActivity(), "连接数据库失败", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    progressDialog.dismiss();
                    Log.d(TAG, response.toString());
                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void getDetail(){
//        RequestParams params = new RequestParams();
//        params.put("api_key", Constant.Key);
//        params.put("api_secret",Constant.Secret);
//        params.put("outer_id",Constant.outer_id);
//        String url="https://api-cn.faceplusplus.com/facepp/v3/faceset/getdetail";
//        HttpUtil.post(url,params,new AsyncHttpResponseHandler(){
//            @Override
//            public void onSuccess(String resultJson) {
//                super.onSuccess(resultJson);
//                Log.d("def",resultJson.toString());
////                result.setText(resultJson.toString());
//            }
//
//            @Override
//            public void onFailure(Throwable throwable, String s) {
//                super.onFailure(throwable, s);
//                Log.d("de",s.toString());
//            }
//        });
//    }

    private void sendFaceToken(String faceToken) {
        OkHttpUtils.post()
                .url(Constant.addfaceUrl)
                .addParams("api_key", Constant.Key)
                .addParams("api_secret", Constant.Secret)
                .addParams("outer_id", Constant.outer_id)
                .addParams("face_tokens", faceToken)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "存入人脸集合失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.d(TAG, response.toString());
//                try {
//                    JSONObject result = new JSONObject(response);
//                    String faceSetToken = result.getString("faceset_token");
//                    String outId = result.getString("outer_id");
//                    Log.d("outid",outId);
//                    Log.d("facesettoken",faceSetToken);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });


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
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            FaceCreatFragment.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            chooseAlbum();
                        }
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
//                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                Uri imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"image.jpg"));
//                //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
//                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                startActivityForResult(openCameraIntent, TAKE_PICTURE);
//
                File outputImage = new File(getActivity().getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(getActivity(), "com.example.cameraablumtest.fileprovider", outputImage);
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    try {
                        // 将拍摄的照片显示出来

                        final Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));//得到照片的bitmap
                        //  final Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);//处理一下照片
                        //  bitmap.recycle();
                        iv_face.setImageBitmap(bitmap);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String imageName = String.valueOf(System.currentTimeMillis());//获得照片的名字
                                ImageTools.savePhotoToSDCard(bitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), imageName);//将照片保存到本地
                                Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + imageName + ".jpg");
                            }
                        }).start();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CHOOSE_PICTURE:
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                    break;
                default:
                    break;
            }
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                case TAKE_PICTURE:
//
//                    try {
//                        //将拍摄的照片显示出来
//                        Bitmap bitmap= BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
//                        Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
//                       //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//                        bitmap.recycle();
//
//                        //将处理过的图片显示在界面上，并保存到本地
////                        Log.d(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
////                        Log.d(TAG, String.valueOf(System.currentTimeMillis()));
////                        ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));
//                        iv_face.setImageBitmap(newBitmap);
//                    }catch (FileNotFoundException e){
//                        e.printStackTrace();
//                    }
//
//                    break;
//
//                case CHOOSE_PICTURE:
//                    ContentResolver resolver = getActivity().getContentResolver();
//                    //照片的原始资源地址
//                    Uri originalUri = data.getData();
//                    try {
//                        //使用ContentProvider通过URI获取原始图片
//                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
//                        if (photo != null) {
//                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
//                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
//                            //释放原始图片占用的内存，防止out of memory异常发生
//                            photo.recycle();
//
//                            iv_face.setImageBitmap(smallBitmap);
//                        }
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//
//                default:
//                    break;
//            }
//        }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePicture();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(final String imagePath) {
        if (imagePath != null) {
            Log.d(TAG, imagePath);
            imageAbsolutePath = imagePath;
              Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            iv_face.setImageBitmap(bitmap);
            Log.d(TAG, FileSizeUtil.getFileOrFilesSize(imagePath));
//            CompressImage.getInstance().imageLubrnCompress(imagePath, imagePath, new CompressLubanListener() {
//                @Override
//                public void onCompressLubanSuccessed(String s, Bitmap bitmap) {
//                  //  Bitmap bitmap = BitmapFactory.decodeFile(bitmap);
//                    Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
//                    bitmap.recycle();
//                    iv_face.setImageBitmap(smallBitmap);
//                    Log.d(TAG, FileSizeUtil.getFileOrFilesSize(s));
//                }
//
//                @Override
//                public void onCompressLubanFailed(String s, String s1) {
//
//                }
//            });

        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();
        }
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
