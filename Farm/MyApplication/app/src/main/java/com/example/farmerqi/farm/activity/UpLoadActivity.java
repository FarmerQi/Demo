package com.example.farmerqi.farm.activity;

import android.Manifest;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.GridViewAdapter;
import com.example.farmerqi.farm.utils.UriToPathUtil;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by FarmerQi on 2018/4/8.
 */
@RuntimePermissions
public class UpLoadActivity extends AppCompatActivity implements View.OnClickListener,AMapLocationListener{
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String RESULT_TAG = "responce";


    private ImageView backImage;
    private EditText uploadEditText;
    private GridView uploadGridView;
    private GridViewAdapter gridViewAdapter;
    private Button selectButton;
    private List<Uri> list;
    private List<Uri> compressedPhotos = new ArrayList<>();
    private TextView titleText;

    private ImageView getLocationImage;
    private TextView locationInfoText;

    public AMapLocationClient aMapLocationClient = null;
    public AMapLocationClientOption aMapLocationClientOption = null;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_layout);
        Intent title = getIntent();
        Bundle message = title.getExtras();
        String titleName = message.getString("title");

        uploadEditText = (EditText)findViewById(R.id.introduce_edit_text);
        uploadGridView = (GridView)findViewById(R.id.image_grid_view);

        //获取地理位置按钮
        getLocationImage = (ImageView)findViewById(R.id.upload_activity_getlocation_image);
        getLocationImage.setOnClickListener(this);

        //地理位置信息
        locationInfoText = (TextView)findViewById(R.id.upload_activity_location_result_text);

        selectButton = (Button)findViewById(R.id.upload_activity_release_button);
        selectButton.setOnClickListener(this);

        backImage = (ImageView)findViewById(R.id.upload_back_image);
        backImage.setOnClickListener(this);

        titleText = (TextView)findViewById(R.id.upload_activity_title);
        titleText.setText(titleName);
        /**获取Drawable目录下的图片的URI的方法*/
//        list = new ArrayList<>();
//        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
//        + getResources().getResourcePackageName(R.drawable.add) + "/"
//        + getResources().getResourceTypeName(R.drawable.add) + "/"
//        + getResources().getResourceEntryName(R.drawable.add));
//        list.add(uri);



        gridViewAdapter = new GridViewAdapter(UpLoadActivity.this,list);
        uploadGridView.setAdapter(gridViewAdapter);
        gridViewAdapter.notifyDataSetChanged();

        uploadGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int itemPosition = position;
                if (position == parent.getCount() - 1){
                    UpLoadActivityPermissionsDispatcher.getPicWithPermissionCheck(UpLoadActivity.this);
                }else {

                }
            }
        });


    }

    //初始化地理位置
    @Override
    protected void onResume() {
        super.onResume();
        UpLoadActivityPermissionsDispatcher.getLocationWithPermissionCheck(UpLoadActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload_activity_release_button:
                for (int i = 0; i < compressedPhotos.size(); i++) {
                    Uri result = compressedPhotos.get(i);
                    sendPic(new File(UriToPathUtil.getRealFilePath(UpLoadActivity.this,result)));
                    Log.e("路径是.........",compressedPhotos.get(i).toString());
                }
                finish();
                break;

            case R.id.upload_back_image:
                finish();
                break;

            case R.id.upload_activity_getlocation_image:
                UpLoadActivityPermissionsDispatcher.getLocationWithPermissionCheck(UpLoadActivity.this);
                break;

        }
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA})
    public void getPic(){

        Matisse.from(this)
                .choose(MimeType.allOf())
                .capture(true)
                .captureStrategy(new CaptureStrategy(true,"com.example.farmerqi.farm.fileProvider"))
                .maxSelectable(20)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UpLoadActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
        UpLoadActivityPermissionsDispatcher.onRequestPermissionsResult(UpLoadActivity.this,requestCode,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            list = Matisse.obtainResult(data);
            for (int i = 0; i < list.size(); i++) {
                Log.e("Uri from camera",list.get(i).toString());
                Log.e("Uri path",list.get(i).getPath());
                Log.e("uri authority",list.get(i).getAuthority());
                Log.e("uri scheme",list.get(i).getScheme());
                Log.e("uri data",list.get(i).getLastPathSegment());
                Log.e("uri data",list.get(i).getEncodedPath());
                Log.e("SDCARDpATH", Environment.getExternalStorageDirectory().toString());

                /**
                 * 此处处理在获取到相机拍摄的照片后，将获取的照片添加到手机媒体文件的数据库中
                 * 此步骤十分关键，如果不进行此步骤的处理，会导致无法获取已经拍摄的照片*/
                if ("com.example.farmerqi.farm.fileProvider".equals(list.get(i).getAuthority())){
                    String[] paths = {Environment.getExternalStorageDirectory().toString() + File.separator + list.get(i).getLastPathSegment()} ;
                    String[] mimeTypes = {MimeType.JPEG.toString(),MimeType.PNG.toString(),MimeType.GIF.toString()};
                    MediaScannerConnection.scanFile(UpLoadActivity.this, paths, mimeTypes, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
                }

                //Log.e("Uri documentId",DocumentsContract.getDocumentId(list.get(i)));
            }
//            gridViewAdapter = new GridViewAdapter(UpLoadActivity.this,list);
//            uploadGridView.setAdapter(gridViewAdapter);
//            gridViewAdapter.notifyDataSetChanged();
            compressPics(list);

        }
    }

    //压缩图片
    //使用Handler机制，在所有线程执行完成之后，再进行UI界面的初始化。
    private List<Uri> compressPics(List<Uri> input){
        final LinkedList<Runnable> taskList = new LinkedList<>();
        final List<Uri> resultList = new ArrayList<>();
        final Handler handler = new Handler();
        class Task implements Runnable{
            Uri uri;
            Task(Uri uri){
                this.uri = uri;
            }
            @Override
            public void run() {
                Luban.with(UpLoadActivity.this)
                        .load(UriToPathUtil.getRealFilePath(UpLoadActivity.this,uri))
                        .setCompressListener(new OnCompressListener() {
                            @Override
                            public void onStart() {

                            }
                            /**
                             * 在所有线程完成之后再进行UI界面的初始化，在这里使用了Handler机制，在
                             * 队列的线程执行完成之后将UI界面初始化
                             * 佩服这位大神!!!
                             * https://blog.csdn.net/lain_comeon/article/details/72770384*/
                            @Override
                            public void onSuccess(File file) {
                                resultList.add(Uri.fromFile(file));
                                Log.e("URI...",Uri.fromFile(file).toString());
                                if (!taskList.isEmpty()){
                                    Runnable runnable = taskList.pop();
                                    handler.post(runnable);
                                }else {
                                    if (compressedPhotos.isEmpty()){
                                        compressedPhotos = resultList;
                                    }else {
                                        compressedPhotos.addAll(resultList);
                                    }

                                    gridViewAdapter = new GridViewAdapter(UpLoadActivity.this,compressedPhotos);
                                    uploadGridView.setAdapter(gridViewAdapter);
                                    gridViewAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        }).launch();
            }
        }
        for (Uri uri : input){
            taskList.add(new Task(uri));
        }
        handler.post(taskList.pop());
        /**
         * 此处错误原因是线程在执行，此时的resultList并未初始化，导致现在的list为空，因此无法实现
         * 获取URI的操作*/
//        for (int i = 0; i < resultList.size(); i++) {
//            Log.e("Uri....","" + resultList.get(i).toString());
//        }
        //Log.e("执行结果","" + resultList.size());
        return resultList;
    }


    //将URI转换为File


    /**
     * 获取地理位置*/
    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE})
    public void getLocation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                aMapLocationClient = new AMapLocationClient(getApplicationContext());
                aMapLocationClientOption = new AMapLocationClientOption();

                //设置定位模式为高精准模式
                aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                aMapLocationClientOption.setNeedAddress(true);
                aMapLocationClientOption.setOnceLocation(true);

                aMapLocationClient.setLocationOption(aMapLocationClientOption);
                //设置定位监听
                aMapLocationClient.setLocationListener(UpLoadActivity.this);
                aMapLocationClient.startLocation();
            }
        }).start();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocationClient != null){
            if (aMapLocation !=null && aMapLocation.getErrorCode() == 0){
                locationInfoText.setText(aMapLocation.getProvince() + "," + aMapLocation.getCity() + "," + aMapLocation.getDistrict() + "," + aMapLocation.getStreet());
            }
        }
    }

    /**
     *
     * 发送图片*/
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



}
