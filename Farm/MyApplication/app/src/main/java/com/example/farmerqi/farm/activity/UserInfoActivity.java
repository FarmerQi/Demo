package com.example.farmerqi.farm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.ViewPagerAdapter;
import com.example.farmerqi.farm.fragment.UserInfoBuyFragment;
import com.example.farmerqi.farm.fragment.UserInfoSaleFragment;
import com.example.farmerqi.farm.model.Location;
import com.example.farmerqi.farm.model.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener,UserInfoBuyFragment.getBuyProducts,UserInfoSaleFragment.getSaleProducts{

    private Handler userHandler;
    private Handler buyProductsHandler;
    private Handler saleProductsHandler;
    private CircleImageView userPic;
    private TextView userLocation;
    private TextView userPhone;
    private ImageView back;
    private ViewPager viewPager;


    private List<Fragment> list = new ArrayList<>();
    private UserInfoBuyFragment userInfoBuyFragment;
    private UserInfoSaleFragment userInfoSaleFragment;
    private ViewPagerAdapter adapter;

    private TextView buyText;
    private TextView saleText;

    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //获取用户ID
        Intent intent = getIntent();
        userID = intent.getIntExtra("userID",1);
        getUser(userID);

        //初始化控件
        userPic = (CircleImageView)findViewById(R.id.product_info_user_pic);
        userLocation = (TextView)findViewById(R.id.product_info_location_text);
        userPhone = (TextView)findViewById(R.id.product_info_user_phone);
        back = (ImageView)findViewById(R.id.upload_back_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        buyText = (TextView)findViewById(R.id.buy_text);
        buyText.setOnClickListener(this);
        saleText = (TextView)findViewById(R.id.sale_text);
        saleText.setOnClickListener(this);


        userInfoBuyFragment = new UserInfoBuyFragment();
        userInfoSaleFragment = new UserInfoSaleFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("user",userID);
        userInfoSaleFragment.setArguments(bundle);
        userInfoBuyFragment.setArguments(bundle);

        list.add(userInfoBuyFragment);
        list.add(userInfoSaleFragment);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),list);
        viewPager = (ViewPager)findViewById(R.id.user_info_viewpager);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());

        viewPager.setCurrentItem(0);
        buyText.setTextColor(this.getResources().getColor(R.color.skyblue));

//        productsRecyclerView = (RecyclerView)findViewById(R.id.user_products_recycler_view);
//        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        productsRecyclerView.setHasFixedSize(false);
//        productsRecyclerView.setItemViewCacheSize(10);
//        //解决滑动冲突
//        productsRecyclerView.setNestedScrollingEnabled(false);
        userHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        User user = (User) msg.obj;
                        if (user.getPicLocation()!=null){
                            Picasso.get().load(user.getPicLocation()).into(userPic);
                        }else {
                            Picasso.get().load(R.drawable.camera).into(userPic);
                        }
                        if (user.getPhoneNum() != null){
                            userPhone.setText(user.getPhoneNum());
                        }else {
                            userPhone.setText("用户未提供电话");
                        }
                        if (user.getLocation()!=null){
                            Location location = user.getLocation();
                            userLocation.setText(location.getProvince()
                                    + location.getCity()
                                    + location.getStreet());
                        }else {
                            userLocation.setText("未获取地址");
                        }
                        break;


                }

            }
        };
    }

    public void getUser(final int id){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/user/findById/" + id).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("message","Fail to connect!");
                            //Toast.makeText(getContext(),"连接服务器失败！",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.e(OKHTTP_MESSAGE,response.body().string());
                            User output = JSON.parseObject(response.body().string(), new TypeReference<User>(){});
                            Message message = userHandler.obtainMessage();
                            message.obj = output;
                            message.what = 0;
                            userHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buy_text:
                viewPager.setCurrentItem(0);
                buyText.setTextColor(getResources().getColor(R.color.skyblue));
                saleText.setTextColor(Color.BLACK);
                break;
            case R.id.sale_text:
                viewPager.setCurrentItem(1);
                buyText.setTextColor(Color.BLACK);
                saleText.setTextColor(getResources().getColor(R.color.skyblue));
                break;
        }
    }

    @Override
    public int getSaleProducts() {
        return userID;
    }

    @Override
    public int getBuyProducts() {
        return userID;
    }


    class MyPagerChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    buyText.setTextColor(getResources().getColor(R.color.skyblue));
                    saleText.setTextColor(Color.BLACK);
                    break;
                case 1:
                    buyText.setTextColor(Color.BLACK);
                    saleText.setTextColor(getResources().getColor(R.color.skyblue));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
