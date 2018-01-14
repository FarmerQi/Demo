package com.example.farmerqi.farm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerqi.farm.utils.PictureUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2018/1/9.
 */

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String RESULT_TAG = "responce";
    private static final int REQUEST_PHOTO = 1;

    private EditText picID;
    private EditText userName;
    private EditText password;
    private EditText phoneNumber;
    private EditText idNumber;
    private EditText address;
    private Button submitButton;
    private TextView resultText;
    private ImageView imageView;
    private ImageButton imageButton;
    Map<String,String> result = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initView();
        updatePhotoView();
        submitButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);
    }
    //初始化控件
    public void initView(){
        picID = (EditText)findViewById(R.id.pic_id_text);
        userName = (EditText)findViewById(R.id.user_name_text);
        password = (EditText)findViewById(R.id.user_psw_text);
        phoneNumber = (EditText)findViewById(R.id.user_phone_text);
        idNumber = (EditText)findViewById(R.id.user_identity_text);
        address = (EditText)findViewById(R.id.user_addr_text);
        submitButton = (Button)findViewById(R.id.submit_button);
        resultText = (TextView)findViewById(R.id.result_text);
        imageButton = (ImageButton)findViewById(R.id.image_button);
        imageView = (ImageView)findViewById(R.id.image_view);
    }
    //采用OkHttp发送请求

    private String initData(){
        Gson request = new Gson();
        result.put("UserPicID",picID.getText().toString());
        result.put("UserName",userName.getText().toString());
        result.put("UserPsw",password.getText().toString());
        result.put("PhoneNum",phoneNumber.getText().toString());
        result.put("Identity",idNumber.getText().toString());
        result.put("Addr",address.getText().toString());
        return request.toJson(result);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_button:
                Toast.makeText(SecondActivity.this,initData(),Toast.LENGTH_SHORT).show();
                sendRequest(initData());
                break;
            case R.id.image_button:
                testTakePhotoPermission();
                break;
        }
    }



    //申请权限完成后的回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PHOTO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                takePhoto();
            } else {
                Toast.makeText(SecondActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //检测是否获取拍照权限
    private void testTakePhotoPermission(){
        if (ContextCompat.checkSelfPermission(SecondActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SecondActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_PHOTO);
        }else {
            takePhoto();
        }
    }
    //进行拍照
    private void takePhoto(){
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = initImageFile();
        Uri uri = FileProvider.getUriForFile(SecondActivity.this, "com.example.farmerqi.farm.provider", imageFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivity = getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo activity : cameraActivity) {
            grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(captureImage, REQUEST_PHOTO);
        }
    }

    //初始化拍照存储的路径，并返回新的文件
    private File initImageFile(){
        File imagePath = new File(getApplicationContext().getFilesDir(),"images");
        File imageFile = new File(imagePath,"test.jpg");
        return imageFile;
    }

    //初始化图像
    private void updatePhotoView(){
        if (initImageFile() == null || initImageFile().exists()){
            imageView.setImageDrawable(null);
        }else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(initImageFile().getPath(),SecondActivity.this);
            imageView.setImageBitmap(bitmap);
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(SecondActivity.this,"com.example.farmerqi.farm.provider",initImageFile());
            SecondActivity.this.revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    //使用OkHttp3发送json数据类型的请求
    private void sendRequest(final String input){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build();
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),input);
                    Request request = new Request.Builder().url("http://farmerqi.imwork.net/user").post(requestBody).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(RESULT_TAG,"fail to connnect");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(RESULT_TAG,"GET RESPONCE");
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
