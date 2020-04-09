package com.example.myapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import Decoder.BASE64Encoder;


public class Constant {
    //设置两个之前获取的两个常量
    //测试版的key
    public static final String Key="4lJMwC1fkXHIHQxx9DXmggN9ZUu1X6Os";//Face++zhe正式的key
    public static final String Secret="7y2ScYT7pFDM2xY_tWM5-FDspRiZi75O";//我的Secret

    public static String facesetToken="";

    //服务器地址
    public static String htUrl="http://139.196.12.165:8080/AndroidTest/";

    //进行人脸检测和人脸分析
    public static  final String detectUrl = "https://api-cn.faceplusplus.com/facepp/v3/detect" ;
    //为一个已经创建的 FaceSet 添加人脸标识 faHttpUtilce_token
    public static final String addfaceUrl = "https://api-cn.faceplusplus.com/facepp/v3/faceset/addface" ;
    //在一个已有的 FaceSet 中找出与目标人脸最相似的一张或多张人脸，返回置信度和不同误识率下的阈值。
    public static final String searchUrl = "https://api-cn.faceplusplus.com/facepp/v3/search" ;
    //创建一个人脸的集合 FaceSet，用于存储人脸标识 face_token
    public static final String createUrl = "https://api-cn.faceplusplus.com/facepp/v3/faceset/create" ;
    //人脸对比API
    public static final String compareUrl="https://api-cn.faceplusplus.com/facepp/v3/compare";
    //人脸集合标识
    public static final String outer_id="FaceSetByme";


    /**
     * TODO:将图片以Base64方式编码为字符串
     * @param imgUrl 图片的绝对路径（例如：D:\\jsontest\\abc.jpg）
     * @return 编码后的字符串
     * @throws IOException
     * */
    public static String encodeImage(String imgUrl) throws IOException {
        FileInputStream fis = new FileInputStream(imgUrl);
        byte[] rs = new byte[fis.available()];
        fis.read(rs);
        fis.close();
        return encode(rs);
    }

    /**
     * TODO:将byte数组以Base64方式编码为字符串
     * @param bytes 待编码的byte数组
     * @return 编码后的字符串
     * */
    public static String encode(byte[] bytes){
        return new BASE64Encoder().encode(bytes);
    }

    /**
     * 将Bitmap转换成字符串
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10;  // 图片的大小设置为原来的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }
    /**
     * 读取图片的旋转的角度
     *
     * @param path
     *            图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);//NORMAL 为0，即不旋转
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90://右旋90度
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
