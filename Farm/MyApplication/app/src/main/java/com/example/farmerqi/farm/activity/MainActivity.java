package com.example.farmerqi.farm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.fragment.BuyFragment;
import com.example.farmerqi.farm.fragment.MessageFragment;
import com.example.farmerqi.farm.fragment.SaleFragment;
import com.example.farmerqi.farm.fragment.SendFragment;
import com.example.farmerqi.farm.model.Picture;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    ImageView userImage;
    RecyclerView recyclerView;
    RelativeLayout user;
    RelativeLayout login;
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
    SendFragment sendFragment;
    MessageFragment messageFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_nav_main);

        user = (RelativeLayout) findViewById(R.id.fourth_button);
        user.setOnClickListener(this);

        login = (RelativeLayout) findViewById(R.id.third_button);
        login.setOnClickListener(this);

        buyFragmnetButton = (RelativeLayout)findViewById(R.id.first_button);
        buyFragmnetButton.setOnClickListener(this);

        saleFragmentButton = (RelativeLayout)findViewById(R.id.second_button);
        saleFragmentButton.setOnClickListener(this);

        sendFragmentButton = (RelativeLayout)findViewById(R.id.fifth_button);
        sendFragmentButton.setOnClickListener(this);


        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout mDrawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_layout_open_state,R.string.drawer_layout_close_state);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        View headLayout = navigationView.getHeaderView(0);
        userImage = (ImageView)headLayout.findViewById(R.id.nav_user_image);
        navigationView.setNavigationItemSelectedListener(this);
        userImage.setOnClickListener(this);

        mainFragmentManager = getSupportFragmentManager();
        showFragment(1);
//        Fragment firstFragment = mainFragmentManager.findFragmentById(R.id.fragment_container_mainActivity);
//        if (firstFragment == null){
//            firstFragment = new BuyFragment();
//            mainFragmentManager.beginTransaction().add(R.id.fragment_container_mainActivity,firstFragment).commit();
//        }

        /*
        测试代码，从服务器获取Picture对象，并在RecyclerView中显示
         */
//        sendButton = (Button)findViewById(R.id.send_button);
//        sendButton.setOnClickListener(this);
//        recyclerView = (RecyclerView)findViewById(R.id.main_recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(false);
//        recyclerView.setItemViewCacheSize(10);

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
            case R.id.Second_item:
            case R.id.Third_item:
            case R.id.Fourth_item:
            case R.id.fifth_item:
                Toast.makeText(this,"Hello,FarmerQi!",Toast.LENGTH_SHORT).show();
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
                if (buyFragment != null){
                    mainFragmentTransaction.show(buyFragment);
                }else {
                    buyFragment = new BuyFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,buyFragment);

                }
                break;

            case 2:

                if (saleFragment != null){
                    mainFragmentTransaction.show(saleFragment);
                }else {
                    saleFragment = new SaleFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,saleFragment);
                }
                break;

            case 3:
                if (sendFragment != null){
                    mainFragmentTransaction.show(sendFragment);
                }else {
                    sendFragment = new SendFragment();
                    mainFragmentTransaction.add(R.id.fragment_container_mainActivity,sendFragment);
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
        if (sendFragment != null){
            fragmentTransaction.hide(sendFragment);
        }
        if (messageFragment != null){
            fragmentTransaction.hide(messageFragment);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_user_image:
                Toast.makeText(this,"hi,FarmerQi",Toast.LENGTH_SHORT).show();
                break;

            case R.id.first_button:
                Toast.makeText(this,"打开BuyFragment",Toast.LENGTH_SHORT).show();
                showFragment(1);
                break;

            case R.id.second_button:
                Toast.makeText(this,"打开SaleFragment",Toast.LENGTH_SHORT).show();
                showFragment(2);
                break;

            case R.id.third_button:
                Toast.makeText(this,"打开sendFragment",Toast.LENGTH_SHORT);
                showFragment(3);
                break;

            case R.id.fourth_button:
                Toast.makeText(this,"打开messageFragment",Toast.LENGTH_SHORT);
                showFragment(4);
//                Toast.makeText(this,"打开第二个活动",Toast.LENGTH_SHORT).show();
//                Intent toSecondActivity = new Intent(MainActivity.this,SecondActivity.class);
//                startActivity(toSecondActivity);
                break;

            case R.id.fifth_button:
                Toast.makeText(this,"打开登录界面",Toast.LENGTH_SHORT).show();
                Intent toLoginActivity = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(toLoginActivity);
                break;

        }

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
