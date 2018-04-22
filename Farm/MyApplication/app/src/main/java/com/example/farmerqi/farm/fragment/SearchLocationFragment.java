//package com.example.farmerqi.farm.fragment;
//
//import android.Manifest;
//import android.content.Context;
//import android.graphics.Point;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.PopupWindow;
//import android.widget.TextView;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.LocationSource;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.Projection;
//import com.amap.api.maps.UiSettings;
//import com.amap.api.maps.model.BitmapDescriptorFactory;
//import com.amap.api.maps.model.LatLng;
//import com.amap.api.maps.model.Marker;
//import com.amap.api.maps.model.MarkerOptions;
//import com.amap.api.maps.model.MyLocationStyle;
//import com.amap.api.services.cloud.CloudItem;
//import com.amap.api.services.cloud.CloudItemDetail;
//import com.amap.api.services.cloud.CloudResult;
//import com.amap.api.services.cloud.CloudSearch;
//import com.amap.api.services.core.AMapException;
//import com.amap.api.services.core.LatLonPoint;
//import com.example.farmerqi.farm.R;
//
//import com.example.farmerqi.farm.adapter.LocationResultAdapter;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Locale;
//
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.RuntimePermissions;
//
///**
// * Created by FarmerQi on 2018/4/15.
// */
//@RuntimePermissions
//public class SearchLocationFragment extends Fragment implements View.OnClickListener, LocationSource, AMapLocationListener, CloudSearch.OnCloudSearchListener {
//
//
//
//    //声明AMapLocationClient类对象
//    public AMapLocationClient aMapLocationClient = null;
//    public AMapLocationClientOption aMapLocationClientOption = null;
//
//    //aMap对象
//    AMap aMap = null;
//
//    private MapView mapView;
//    //设置小蓝点的回调监听对象
//    private OnLocationChangedListener mListener;
//    private CloudSearch cloudSearch;
//
//    //屏幕中心点的经纬度
//    private double latitude;
//    private double longitude;
//
//    //搜索控件
//    private Button locationButton;
//
//    //UI控件
//    UiSettings uiSettings;
//
//    //搜索结果
//    PopupWindow resultPopupWindow;
//    RecyclerView resultRecyclerView;
//    TextView resultTextView;
//    ImageView quitImage;
//
//    //ToolBar和SearchView
//    android.support.v7.widget.Toolbar toolbar;
//
//
//
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_search_location,container,false);
//
//        locationButton = (Button)view.findViewById(R.id.getLocation_button);
//        locationButton.setOnClickListener(this);
//
//        mapView = (MapView)view.findViewById(R.id.location_mapview);
//
//        //初始化，并且在后台情况下保存地图状态
//        mapView.onCreate(savedInstanceState);
//
//        if (aMap == null){
//            aMap = mapView.getMap();
//        }
//        aMap.setLocationSource(this);
////        //设置显示指南针按钮
////        uiSettings.setCompassEnabled(true);
////        //显示默认的定位按钮
////        uiSettings.setMyLocationButtonEnabled(true);
//        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationEnabled(true);
//        return view;
//    }
//
//
//
//    /**
//     * by moos on 2017/09/05
//     * func:获取屏幕中心的经纬度坐标
//     * @return
//     */
//    public LatLng getMapCenterPoint() {
//        int left = mapView.getLeft();
//        int top = mapView.getTop();
//        int right = mapView.getRight();
//        int bottom = mapView.getBottom();
//        // 获得屏幕点击的位置
//        int x = (int) (mapView.getX() + (right - left) / 2);
//        int y = (int) (mapView.getY() + (bottom - top) / 2);
//        Projection projection = aMap.getProjection();
//        LatLng pt = projection.fromScreenLocation(new Point(x, y));
//        return pt;
//    }
//
//    //重写Activity的生命周期过程，同时保证地图的状态和活动状态保持一致
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
//        mapView.onSaveInstanceState(outState);
//    }
//
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.getLocation_button:
//                SearchLocationFragmentPermissionsDispatcher.getLocationWithPermissionCheck(this);
//                break;
//        }
//    }
//
//    //开始定位的线程
//    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.READ_PHONE_STATE})
//    public void getLocation(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                aMapLocationClient = new AMapLocationClient(getActivity());
//                aMapLocationClientOption = new AMapLocationClientOption();
//
//                //设置定位模式为高精准模式
//                aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//                aMapLocationClientOption.setNeedAddress(true);
//                aMapLocationClientOption.setOnceLocation(true);
//
//                aMapLocationClient.setLocationOption(aMapLocationClientOption);
//                //设置定位监听
//                aMapLocationClient.setLocationListener(SearchLocationFragment.this);
//                aMapLocationClient.startLocation();
//            }
//        }).start();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        SearchLocationFragmentPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
//        //LocationActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
//    }
//
//    private static SimpleDateFormat simpleDateFormat = null;
//
//    public static String formateDate(long l,String strPattern){
//        if (simpleDateFormat == null){
//            try {
//                simpleDateFormat = new SimpleDateFormat(strPattern, Locale.CHINA);
//            }catch (Throwable throwable){
//
//            }
//
//        }else {
//            simpleDateFormat.applyPattern(strPattern);
//        }
//        return simpleDateFormat == null ? "NULL" : simpleDateFormat.format(l);
//    }
//
//    /**
//     * 在aMap.setLocationSource(this)中包含两个回调，activate(OnLocationChangedListener)和deactivate()。
//     在activate()中设置定位初始化及启动定位，在deactivate()中写停止定位的相关调用。*/
//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//        //开始定位
//        //LocationActivityPermissionsDispatcher.getLocationWithPermissionCheck(SearchLocationFragment.this);
//        SearchLocationFragmentPermissionsDispatcher.getLocationWithPermissionCheck(this);
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//        if (aMapLocationClient != null){
//            aMapLocationClient.stopLocation();
//            aMapLocationClient.onDestroy();
//        }
//        aMapLocationClient = null;
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocationClient != null){
//            if (mListener != null && aMapLocation != null){
//                if (aMapLocation.getErrorCode() == 0){
//                    //设置显示当前位置
//                    MyLocationStyle myLocationStyle = new MyLocationStyle();
//                    //定位一次，且将视角移动到地图中心点。
//                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
//                    aMap.setMyLocationStyle(myLocationStyle);
//                    latitude = aMapLocation.getLatitude();
//                    longitude = aMapLocation.getLongitude();
//                    Log.e("Location","国家:" + aMapLocation.getCountry() + "\n"
//                            + "省份："+aMapLocation.getProvince() + "\n"
//                            + "市区：" + aMapLocation.getCity() + "\n"
//                            + "城市编码: " + aMapLocation.getCityCode() + "\n"
//                            + "区：" + aMapLocation.getDistrict() + "\n"
//                            + "区域码：" + aMapLocation.getAdCode() + "\n"
//                            + "地址: " + aMapLocation.getAddress() + "\n"
//                            + "定位时间: " + formateDate(aMapLocation.getTime(),"yyyy-MM-dd HH:mm:ss") + "\n" );
//                    mListener.onLocationChanged(aMapLocation);
//                }else {
//                    Log.e("AmpError","location Error,ErrorCode:"
//                            + aMapLocation.getErrorCode() + ",errInfo:"
//                            + aMapLocation.getErrorInfo());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        /**
//         * 如果AMapLocationClient是在当前Activity实例化的，
//         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
//         */
//        if (aMapLocationClient != null){
//            aMapLocationClient.stopLocation();
//            aMapLocationClient.onDestroy();
//            aMapLocationClient = null;
//            aMapLocationClientOption = null;
//        }
//        aMap = null;
//        mapView.onDestroy();
//
//    }
//
//    //搜索周边
//    private void searchAround(String input){
//        //搜索
//        cloudSearch = new CloudSearch(getContext());
//        cloudSearch.setOnCloudSearchListener(this);
//
//        CloudSearch.SearchBound bound = new CloudSearch.SearchBound(new LatLonPoint(latitude,longitude),10000);
//        CloudSearch.Query query = null;
//        try {
//            query = new CloudSearch.Query("5ad5ae692376c14947b17452",input,bound);
//        } catch (AMapException e) {
//            e.printStackTrace();
//        }
//        cloudSearch.searchCloudAsyn(query);
//    }
//
//    //搜索结果的回调函数
//    @Override
//    public void onCloudSearched(CloudResult cloudResult, int i) {
//        Log.e("message",cloudResult.getTotalCount() +"");
//        for (CloudItem item: cloudResult.getClouds()) {
//            LatLonPoint temp = item.getLatLonPoint();
//            LatLng tempLatlng = new LatLng(temp.getLatitude(),temp.getLongitude());
//            Log.e("Location Message:",temp.getLatitude() + "  " + temp.getLongitude());
//            Log.e("自定义字段",item.getCustomfield().get("user_pic"));
//            MarkerOptions markerOptions = new MarkerOptions().position(tempLatlng).title(item.getTitle()).snippet(item.getSnippet());
//            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.location)));
//            View markerView = LayoutInflater.from(getContext()).inflate(R.layout.location_icon,null);
//            markerOptions.icon(BitmapDescriptorFactory.fromView(markerView));
//
//            final Marker marker = aMap.addMarker(markerOptions);
//
//        }
//        showPopupWindow(cloudResult.getClouds());
//
//    }
//
//    @Override
//    public void onCloudItemDetailSearched(CloudItemDetail cloudItemDetail, int i) {
//
//    }
//
//    //将结果输入到popupWindow
//    private void showPopupWindow(List<CloudItem> input){
//        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.location_activity_search_result_popup,null);
//        resultTextView = (TextView)contentView.findViewById(R.id.location_result_text_view);
//        resultRecyclerView = (RecyclerView)contentView.findViewById(R.id.location_result_recycler_view);
//        quitImage = (ImageView)contentView.findViewById(R.id.popup_location_quit_image);
//        quitImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                resultPopupWindow.dismiss();
//            }
//        });
//        resultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        resultRecyclerView.setHasFixedSize(false);
//        resultRecyclerView.setItemViewCacheSize(10);
//        if (input.isEmpty() || input == null){
//            resultRecyclerView.setVisibility(View.GONE);
//        }else {
//            resultTextView.setVisibility(View.GONE);
//            LocationResultAdapter adapter = new LocationResultAdapter(input);
//            resultRecyclerView.setAdapter(adapter);
//        }
//        contentView.measure(0,0);
//        //处理popup window弹出的位置。通过组件的getLocationOnScreen方法获取基准组件的位置，之后需要调用
//        //view的measure方法获取需要弹出的组件的具体高度和宽度。在根据自己的需求去安放组件
//        //参考网址：
//        //https://blog.csdn.net/cjllife/article/details/8146185/
//        resultPopupWindow = new PopupWindow(getContext());
//
//        resultPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        resultPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//
//        //popup window 自身问题会导致背景出现黑边，设置背景后可解决
//        resultPopupWindow.setBackgroundDrawable(null);
//
//        resultPopupWindow.setFocusable(true);
//        resultPopupWindow.setContentView(contentView);
//
//
//        //为popupWindow设置入场和出场动画
//        resultPopupWindow.setAnimationStyle(R.style.anim_popup_widow);
//        int[] location = new int[2];
//
//        //searchButton.getLocationOnScreen(location);
//        //resultPopupWindow.showAtLocation(searchButton, Gravity.NO_GRAVITY,location[0],location[1] - contentView.getMeasuredHeight() -20);
//        resultPopupWindow.showAtLocation(LayoutInflater.from(getActivity()).inflate(R.layout.home_page_main,null),Gravity.BOTTOM,0,0);
//        //设置弹出后背景透明度
//        backgroundAlpha(0.7f);
//        resultPopupWindow.setOnDismissListener(new poponDismissListenner());
//
//    }
//
//    //在popupWindow消失后处理背景透明度的方法
//    class poponDismissListenner implements PopupWindow.OnDismissListener{
//
//        @Override
//        public void onDismiss() {
//            backgroundAlpha(1f);
//        }
//    }
//    //设置popupWindow显示后的背景透明度
//    //浮点值，从0.0 - 1.0
//    public void backgroundAlpha(float bgAlpha){
//        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
//        layoutParams.alpha = bgAlpha;
//        getActivity().getWindow().setAttributes(layoutParams);
//    }
//}
