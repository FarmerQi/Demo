package com.example.farmerqi.farm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.fragment.BuyFragment;
import com.example.farmerqi.farm.fragment.MessageFragment;
import com.example.farmerqi.farm.fragment.SaleFragment;


import com.example.farmerqi.farm.model.Picture;



import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2017/12/13.
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    private static final String OKHTTP_MESSAGE = "FROM SERVER:---------";
    private static final String MESSAGE_SIE = "SIZE ____";
    ImageView userImage;
    RecyclerView recyclerView;
    RelativeLayout user;
    RelativeLayout releaseButton;
    RelativeLayout buyFragmnetButton;
    RelativeLayout saleFragmentButton;
    RelativeLayout sendFragmentButton;
    Button sendButton;
    List<Picture> input;
    List<Picture> output = new ArrayList<>();

    //有关Fragment
    FragmentManager mainFragmentManager;
    FragmentTransaction mainFragmentTransaction;
    BuyFragment buyFragment;
    SaleFragment saleFragment;
    MessageFragment messageFragment;



    //popupWindow窗口
    PopupWindow popupWindow;

    //popupWindow的两个图标
    LinearLayout saleButton;
    LinearLayout buyButton;

    //用户头像
    CircleImageView userImageToolbar;

    //DrawerLayout
    DrawerLayout mDrawerLayout;

    //navigationView
    NavigationView navigationView;

    //title
    TextView title;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_nav_main);

        user = (RelativeLayout) findViewById(R.id.message_button);
        user.setOnClickListener(this);


        title = (TextView)findViewById(R.id.main_activity_title);

        //用户头像
        userImageToolbar = (CircleImageView)findViewById(R.id.user_image);
        userImageToolbar.setOnClickListener(this);

        //发布按钮
        releaseButton = (RelativeLayout) findViewById(R.id.third_button);
        releaseButton.setOnClickListener(this);

        //供应界面按钮
        buyFragmnetButton = (RelativeLayout)findViewById(R.id.buy_button);
        buyFragmnetButton.setOnClickListener(this);

        //需求界面按钮
        saleFragmentButton = (RelativeLayout)findViewById(R.id.sale_button);
        saleFragmentButton.setOnClickListener(this);

        //不知道该叫啥的按钮
        sendFragmentButton = (RelativeLayout)findViewById(R.id.search_button);
        sendFragmentButton.setOnClickListener(this);

        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.location);


        mDrawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_layout_open_state,R.string.drawer_layout_close_state);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);

        View headLayout = navigationView.getHeaderView(0);
        userImage = (CircleImageView)headLayout.findViewById(R.id.nav_user_image);
        //在此处加载用户头像

        navigationView.setNavigationItemSelectedListener(this);
        userImage.setOnClickListener(this);

        mainFragmentManager = getSupportFragmentManager();
        showFragment(1);


    }


    //重写该方法，在打开DrawerLayout后，在用户点击返回按钮后，应用不会退出，同时回到之前的界面
    @Override
    public void onBackPressed(){
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.First_item:
                Intent toCollectionActivity = new Intent(MainActivity.this,CollectionActivity.class);
                startActivity(toCollectionActivity);
                break;
            case R.id.Second_item:
                Intent toReleaseActivity = new Intent(MainActivity.this,ReleaseActivity.class);
                startActivity(toReleaseActivity);
                break;
            case R.id.Fourth_item:

                break;
            case R.id.fifth_item:
                Intent toLoginActivity = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(toLoginActivity);
                break;

        }
        DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //配置显示Fragment
    public void showFragment(int index){
        mainFragmentTransaction = mainFragmentManager.beginTransaction();
        hideFragment(mainFragmentTransaction);
        switch (index){

            case 1:
                title.setText("供应信息");
                if (buyFragment != null){
                    mainFragmentTransaction.show(buyFragment);
                }else {
                    buyFragment = new BuyFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,buyFragment);

                }
                break;

            case 2:
                title.setText("需求信息");
                if (saleFragment != null){
                    mainFragmentTransaction.show(saleFragment);
                }else {
                    saleFragment = new SaleFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,saleFragment);
                }
                break;


            case 4:
                if (messageFragment != null){
                    mainFragmentTransaction.show(messageFragment);
                }else {
                    messageFragment = new MessageFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,messageFragment);
                }
                break;

        }
        mainFragmentTransaction.commit();
    }

    //隐藏Fragment
    public void hideFragment(FragmentTransaction fragmentTransaction){
        if (buyFragment != null){
            fragmentTransaction.hide(buyFragment);
        }
        if (saleFragment != null){
            fragmentTransaction.hide(saleFragment);
        }
        if (messageFragment != null){
            fragmentTransaction.hide(messageFragment);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //导航栏的用户头像点击事件
            case R.id.nav_user_image:
                Toast.makeText(this,"hi,FarmerQi",Toast.LENGTH_SHORT).show();
                break;

            //ToolBar上的用户头像点击事件
            case R.id.user_image:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.buy_button:
                Toast.makeText(this,"打开BuyFragment",Toast.LENGTH_SHORT).show();
                showFragment(1);
                break;

            case R.id.sale_button:
                Toast.makeText(this,"打开SaleFragment",Toast.LENGTH_SHORT).show();
                showFragment(2);
                break;
            //POPUP WINDOW的点击事件
            case R.id.third_button:
                showPopupWindow();
                break;

            case R.id.message_button:
                Intent toLoginActivity = new Intent(MainActivity.this,ProductInfoActivity.class);
                startActivity(toLoginActivity);
                break;

            case R.id.search_button:
//                Toast.makeText(this,"打开登录界面",Toast.LENGTH_SHORT).show();
//                Intent toLoginActivity = new Intent(MainActivity.this,LoginActivity.class);
//                startActivity(toLoginActivity);
//                showFragment(3);
                Intent toLocationSearchActivity = new Intent(MainActivity.this,LocationActivity.class);
                startActivity(toLocationSearchActivity);
                break;
            //popupWindow的界面点击事件
            case R.id.popup_buy_linear_layout:
                Intent toBuyUploadActivity = new Intent(MainActivity.this,UpLoadActivity.class);
                toBuyUploadActivity.putExtra("title","发布需求信息");
                startActivity(toBuyUploadActivity);
                Toast.makeText(this,"这是卖东西的按钮",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                break;

            case R.id.popup_sale_linear_layout:

                Intent toSaleUploadActivity = new Intent(MainActivity.this,UpLoadActivity.class);
                toSaleUploadActivity.putExtra("title","发布供应信息");
                startActivity(toSaleUploadActivity);
                Toast.makeText(this,"这是卖东西按钮",Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
                break;
        }

    }

    //展示PopupWindow
    private void showPopupWindow(){

        View contentView = LayoutInflater.from(this).inflate(R.layout.main_activity_cardview_popup,null);
        /*由于加载Activity时，初始化只涉及到当前布局的界面，因此无法获取popupWindow内部的View，
        因此在onCreate方法中初始化这两个控件时，会导致空指针错误。正确做法是在初始化popupWindow时，由popupWindow
        的界面来获取具体的View实例并进行初始化
        */
        //popupWindow的购买按钮
        buyButton = (LinearLayout)contentView.findViewById(R.id.popup_buy_linear_layout);
        buyButton.setOnClickListener(this);

        //popupWindow的卖东西按钮
        saleButton = (LinearLayout)contentView.findViewById(R.id.popup_sale_linear_layout);
        saleButton.setOnClickListener(this);

        contentView.measure(0,0);
        //处理popup window弹出的位置。通过组件的getLocationOnScreen方法获取基准组件的位置，之后需要调用
        //view的measure方法获取需要弹出的组件的具体高度和宽度。在根据自己的需求去安放组件
        //参考网址：
        //https://blog.csdn.net/cjllife/article/details/8146185/
        popupWindow = new PopupWindow(this);

        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);

        //popup window 自身问题会导致背景出现黑边，设置背景后可解决
        popupWindow.setBackgroundDrawable(null);

        popupWindow.setFocusable(true);
        popupWindow.setContentView(contentView);


        //为popupWindow设置入场和出场动画
        popupWindow.setAnimationStyle(R.style.anim_popup_widow);
        int[] location = new int[2];
        releaseButton.getLocationOnScreen(location);
        popupWindow.showAtLocation(releaseButton,Gravity.NO_GRAVITY,location[0],location[1] - contentView.getMeasuredHeight() -20);

        //设置弹出后背景透明度
        backgroundAlpha(0.7f);
        popupWindow.setOnDismissListener(new poponDismissListenner());
        Log.e(MESSAGE_SIE,contentView.getMeasuredHeight() + "");
        Log.e(MESSAGE_SIE,location[0] + "");
        Log.e(MESSAGE_SIE,location[1] + "");
        Log.e(MESSAGE_SIE,releaseButton.getHeight() + "");
        Log.e(MESSAGE_SIE,popupWindow.getHeight() + "");


    }

    //在popupWindow消失后处理背景透明度的方法
    class poponDismissListenner implements PopupWindow.OnDismissListener{
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }
    //设置popupWindow显示后的背景透明度
    //浮点值，从0.0 - 1.0
    public void backgroundAlpha(float bgAlpha){
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.alpha = bgAlpha;
        getWindow().setAttributes(layoutParams);
    }

    //获取返回的json数据
    public List<Picture> getPic(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/pic/findAll").build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(OKHTTP_MESSAGE,"Fail to connect!");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.e(OKHTTP_MESSAGE,response.body().string());
                            output = JSON.parseObject(response.body().string(), new TypeReference<List<Picture>>(){});
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();
        return output;
    }

}


