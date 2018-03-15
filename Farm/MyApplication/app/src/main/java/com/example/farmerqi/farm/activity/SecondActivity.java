package com.example.farmerqi.farm.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.utils.PictureUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by FarmerQi on 2018/1/9.
 */

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String RESULT_TAG = "responce";
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_ALBUM = 2;
    private static final String COMPRESS_START = "开始压缩图片";
    private static final String COMPRESSING = "正在压缩";
    private static final String COMPRESS_RESULT = "压缩完成";

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
    private ImageButton albumImageButton;
    private Button sendImageButton;
    private File imageFile;
    private Uri imageUri;
    Map<String,String> result = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initView();
        submitButton.setOnClickListener(this);
        imageButton.setOnClickListener(this);
        albumImageButton.setOnClickListener(this);
        sendImageButton.setOnClickListener(this);
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
        albumImageButton = (ImageButton)findViewById(R.id.image_button_album);
        sendImageButton = (Button)findViewById(R.id.send_image_button);
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
//                sendRequest(initData());
                break;
            case R.id.image_button:
                testTakePhotoPermission();
                break;
            case R.id.image_button_album:
                testAlbumPermission();
                break;
            case R.id.send_image_button:
                Log.e(RESULT_TAG,"send image to server");
                Log.e(RESULT_TAG,"图片大小为：" + imageFile.length());
                compressPicAndSend(imageFile);
                break;
        }
    }

    //申请权限完成后的回调函数
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case REQUEST_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(SecondActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_ALBUM:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(SecondActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
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

    //检测是否拥有打开相册的权限
    private void testAlbumPermission(){
        if (ContextCompat.checkSelfPermission(SecondActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SecondActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ALBUM);
        }else {
            openAlbum();
        }
    }

    //打开相册
    private void openAlbum(){
        Intent openAlbumIntent = new Intent("android.intent.action.GET_CONTENT");
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent,REQUEST_ALBUM);
    }

    //进行拍照
    private void takePhoto(){
        imageFile = new File(getApplicationContext().getFilesDir(),"test.jpg");//相机拍照的文件
        if (imageFile.exists()){
            imageFile.delete();
        }
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = FileProvider.getUriForFile(SecondActivity.this, "com.example.farmerqi.farm.fileProvider", imageFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        List<ResolveInfo> cameraActivity = getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo activity : cameraActivity) {
            grantUriPermission(activity.activityInfo.packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(captureImage, REQUEST_PHOTO);
        }
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case REQUEST_PHOTO:
                if (resultCode == RESULT_OK){
                    try {
                        Bitmap result = PictureUtils.getScaledBitmap(imageFile.getPath(),SecondActivity.this);
                        //Bitmap resultPic = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(result);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case REQUEST_ALBUM:
                if (resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= 19){
                        handleImageAfterKitKat(data);
                    }else {
                        handleImageBeforeKitKat(data);
                    }
                }

                break;
            default:
                break;
        }

    }

    //处理从相册获取的照片
    //在API>=19时的处理方式
    private void handleImageAfterKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        //如果是document类型的uri，通过document ID处理
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                imagePath = getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath);
    }

    //API低于19时的处理方式
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }
    //通过Uri获取图片路径
    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //根据图片路径处理图片
    private void displayImage(String path){
        if (path != null){
            Bitmap bitmap = PictureUtils.getScaledBitmap(path,this);
            imageView.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this,"fail to get IMAGE!",Toast.LENGTH_LONG).show();
        }
    }
//    //客户端无需发送JSON格式的数据，在服务器端可以按照常规的Http请求的方式来处理这些请求
//    //使用OkHttp3发送json数据类型的请求
//    private void sendRequest(final String input){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
//                            .writeTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build();
//                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"),input);
//                    Request request = new Request.Builder().url("http://farmerqi.imwork.net/user").post(requestBody).build();
//                    okHttpClient.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Log.e(RESULT_TAG,"fail to connnect");
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            Log.d(RESULT_TAG,"GET RESPONCE");
//                        }
//                    });
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    //使用OkHttp3发送图片
    private void sendPic(final File input){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).build();
                    //RequestBody requestBody = FormBody.create(MediaType.parse("multipart/form-data"),input);
                    RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("picture",input.getName(),RequestBody.create(MediaType.parse("multipart/form-data"),input)).build();
                    Request request = new Request.Builder().url("http://192.168.191.1:8080/pic/upload").post(requestBody).build();
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
    public void compressPicAndSend(final File input){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Luban.with(getApplicationContext())
                        .load(input)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {
                                Log.e(COMPRESS_START,"Start compress!!!");
                                Log.e(COMPRESSING,"Compressing....");
                            }

                            @Override
                            public void onSuccess(File file) {
                                Log.e(COMPRESS_RESULT,"Finish");
                                Log.e(COMPRESS_RESULT,file.getPath());
                                Log.e(RESULT_TAG,"压缩后图片大小" + file.length());
                                sendPic(file);

                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).launch();
            }
        }).start();
    }
}
