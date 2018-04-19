package com.example.farmerqi.farm.activity;

import android.Manifest;
import android.graphics.Point;
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
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.cloud.CloudItem;
import com.amap.api.services.cloud.CloudItemDetail;
import com.amap.api.services.cloud.CloudResult;
import com.amap.api.services.cloud.CloudSearch;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.example.farmerqi.farm.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by FarmerQi on 2018/4/15.
 */
@RuntimePermissions
public class LocationActivity extends AppCompatActivity implements View.OnClickListener, LocationSource, AMapLocationListener, CloudSearch.OnCloudSearchListener {

    private TextView resultText;
    private Button locationButton;
    private Button searchButton;
    //声明AMapLocationClient类对象
    public AMapLocationClient aMapLocationClient = null;
    public AMapLocationClientOption aMapLocationClientOption = null;

    //aMap对象
    AMap aMap = null;

    private MapView mapView;
    //设置小蓝点的回调监听对象
    private OnLocationChangedListener mListener;
    private CloudSearch cloudSearch;

    //屏幕中心点的经纬度
    private double latitude;
    private double longitude;

    //搜索结果


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_layout);
        resultText = (TextView)findViewById(R.id.location_message_textVIEW);
        searchButton = (Button)findViewById(R.id.location_search_button);
        searchButton.setOnClickListener(this);
        locationButton = (Button)findViewById(R.id.getLocation_button);
        locationButton.setOnClickListener(this);

        mapView = (MapView)findViewById(R.id.location_mapview);

        //初始化，并且在后台情况下保存地图状态
        mapView.onCreate(savedInstanceState);

        if (aMap == null){
            aMap = mapView.getMap();
        }
        aMap.setLocationSource(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationStyle(myLocationStyle);




    }
    /**
     * by moos on 2017/09/05
     * func:获取屏幕中心的经纬度坐标
     * @return
     */
    public LatLng getMapCenterPoint() {
        int left = mapView.getLeft();
        int top = mapView.getTop();
        int right = mapView.getRight();
        int bottom = mapView.getBottom();
        // 获得屏幕点击的位置
        int x = (int) (mapView.getX() + (right - left) / 2);
        int y = (int) (mapView.getY() + (bottom - top) / 2);
        Projection projection = aMap.getProjection();
        LatLng pt = projection.fromScreenLocation(new Point(x, y));
        return pt;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getLocation_button:
                LocationActivityPermissionsDispatcher.getLocationWithPermissionCheck(LocationActivity.this);
                break;
            case R.id.location_search_button:
                searchAround();
                break;
        }
    }

    //开始定位的线程
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
                aMapLocationClientOption.setOnceLocationLatest(true);

                aMapLocationClient.setLocationOption(aMapLocationClientOption);
                //设置定位监听
                aMapLocationClient.setLocationListener(LocationActivity.this);

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

    /**
     * 在aMap.setLocationSource(this)中包含两个回调，activate(OnLocationChangedListener)和deactivate()。
     在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。*/
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        //开始定位
        LocationActivityPermissionsDispatcher.getLocationWithPermissionCheck(LocationActivity.this);

    }

    @Override
    public void deactivate() {
        mListener = null;
        if (aMapLocationClient != null){
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();

        }

        aMapLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocationClient != null){
            if (mListener != null && aMapLocation != null){
                if (aMapLocation.getErrorCode() == 0){

                    latitude = aMapLocation.getLatitude();
                    longitude = aMapLocation.getLongitude();
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
                    mListener.onLocationChanged(aMapLocation);
                }else {
                    Log.e("AmpError","location Error,ErrorCode:"
                            + aMapLocation.getErrorCode() + ",errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         */
        if (aMapLocationClient != null){
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
            aMapLocationClientOption = null;
        }
        aMap = null;
        mapView.onDestroy();

    }

    //搜索周边
    private void searchAround(){
        //搜索
        cloudSearch = new CloudSearch(LocationActivity.this);
        cloudSearch.setOnCloudSearchListener(this);

        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(latitude,longitude),10000);
        CloudSearch.Query query = null;
        try {
            query = new CloudSearch.Query("5ad5ae692376c14947b17452","西瓜",bound);
        } catch (AMapException e) {
            e.printStackTrace();
        }
        cloudSearch.searchCloudAsyn(query);
    }

    //搜索结果的回调函数
    @Override
    public void onCloudSearched(CloudResult cloudResult, int i) {
        Log.e("message",cloudResult.getTotalCount() +"");
        for (CloudItem item: cloudResult.getClouds()) {
            LatLonPoint temp = item.getLatLonPoint();
            LatLng tempLatlng = new LatLng(temp.getLatitude(),temp.getLongitude());
            Log.e("Location Message:",temp.getLatitude() + "  " + temp.getLongitude());
            final Marker marker = aMap.addMarker(new MarkerOptions().position(tempLatlng).title(item.getTitle()).snippet(item.getSnippet()));

        }

    }

    @Override
    public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int i) {

    }
}
