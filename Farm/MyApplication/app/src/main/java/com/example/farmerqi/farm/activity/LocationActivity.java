package com.example.farmerqi.farm.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.example.farmerqi.farm.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by FarmerQi on 2018/4/15.
 */
@RuntimePermissions
public class LocationActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView resultText;
    private Button locationButton;
    //声明AMapLocationClient类对象
    public AMapLocationClient aMapLocationClient = null;
    public AMapLocationClientOption aMapLocationClientOption = null;

    private Intent alarmIntent = null;
    private PendingIntent alarmPi = null;
    private AlarmManager alarmManager = null;
    private MapView mapView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_layout);
        resultText = (TextView)findViewById(R.id.location_message_textVIEW);
        locationButton = (Button)findViewById(R.id.getLocation_button);
        locationButton.setOnClickListener(this);
        mapView = (MapView)findViewById(R.id.location_mapview);
        //初始化，并且在后台情况下保存地图状态
        mapView.onCreate(savedInstanceState);
        AMap aMap = null;
        if (aMap == null){
            aMap = mapView.getMap();
        }
        aMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        aMapLocationClientOption = new AMapLocationClientOption();
        //设置定位模式为高精准模式
        aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        aMapLocationClientOption.setNeedAddress(true);
        aMapLocationClientOption.setGpsFirst(true);
        aMapLocationClientOption.setOnceLocation(true);

        //设置定位监听
        aMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null){
                    if (aMapLocation.getErrorCode() == 0){
                        Log.e("Location","国家:" + aMapLocation.getCountry() + "\n"
                                + "省份："+aMapLocation.getProvince() + "\n"
                                + "市区：" + aMapLocation.getCity() + "\n"
                                + "城市编码: " + aMapLocation.getCityCode() + "\n"
                                + "区：" + aMapLocation.getDistrict() + "\n"
                                + "区域码：" + aMapLocation.getAdCode() + "\n"
                                + "地址: " + aMapLocation.getAddress() + "\n"
                                + "定位时间: " + formateDate(aMapLocation.getTime(),"yyyy-MM-dd HH:mm:ss") + "\n" );
                        //当amapLocation的错误码为0时，表示定位成功，可以解析该对象
                        resultText.setText("国家:" + aMapLocation.getCountry() + "\n"
                                            + "省份："+aMapLocation.getProvince() + "\n"
                                            + "市区：" + aMapLocation.getCity() + "\n"
                                            + "城市编码" + aMapLocation.getCityCode() + "\n"
                                            + "区：" + aMapLocation.getDistrict() + "\n"
                                            + "区域码：" + aMapLocation.getAdCode() + "\n"
                                            + "地址" + aMapLocation.getAddress() + "\n"
                                            + "定位时间" + formateDate(aMapLocation.getTime(),"yyyy-MM-dd HH:mm:ss") + "\n" );
                    }else {
                        Log.e("AmpError","location Error,ErrorCode:"
                         + aMapLocation.getErrorCode() + ",errInfo:"
                         + aMapLocation.getErrorInfo());
                    }
                }
            }
        });


    }

    //重写Activity的生命周期过程，同时保证地图的状态和活动状态保持一致
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         */
        if (aMapLocationClient != null){
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
            aMapLocationClientOption = null;
        }
        mapView.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getLocation_button:
                LocationActivityPermissionsDispatcher.getLocationWithPermissionCheck(LocationActivity.this);
                break;
        }
    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE})
    public void getLocation(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                aMapLocationClient.setLocationOption(aMapLocationClientOption);
                aMapLocationClient.startLocation();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    private static SimpleDateFormat simpleDateFormat = null;

    public static String formateDate(long l,String strPattern){
        if (simpleDateFormat == null){
            try {
                simpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
            }catch (Throwable throwable){

            }

        }else {
            simpleDateFormat.applyPattern(strPattern);
        }
        return simpleDateFormat == null ? "NULL" : simpleDateFormat.format(l);
    }
}
