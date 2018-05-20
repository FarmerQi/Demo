package com.example.farmerqi.farm.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.MyAdapter;
import com.example.farmerqi.farm.adapter.ProductDetailAdapter;
import com.example.farmerqi.farm.model.Location;
import com.example.farmerqi.farm.model.Product;
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

/**
 * Created by FarmerQi on 2018/4/29.
 */

public class ProductInfoActivity extends AppCompatActivity {

    //有关用户
    CircleImageView userPic;
    TextView userName;
    TextView phoneNum;
    TextView location;

    //有关产品
    ImageView firstProductImage;
    TextView productName;
    TextView productAmount;
    TextView productPrice;
    RecyclerView recyclerView;

    //有关数据
    int userID;
    int productID;

    //Handler
    Handler dataHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        //有关用户
        userPic = (CircleImageView)findViewById(R.id.user_pic);
        userName = (TextView)findViewById(R.id.user_name);
        phoneNum = (TextView)findViewById(R.id.user_phone);
        location = (TextView)findViewById(R.id.user_location);

        //有关产品
        firstProductImage = (ImageView)findViewById(R.id.first_product_image);
        productName = (TextView)findViewById(R.id.product_name);
        productAmount = (TextView)findViewById(R.id.product_amount);
        productPrice = (TextView)findViewById(R.id.product_price);

        recyclerView = (RecyclerView)findViewById(R.id.product_info_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProductInfoActivity.this));
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setNestedScrollingEnabled(false);

        //有关数据处理
        Intent data = getIntent();
        userID = data.getIntExtra("userID",0);
        productID = data.getIntExtra("productID",0);
        getProduct(productID);
        getUser(userID);

        dataHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        User user = (User)msg.obj;
                        Location userLocation = user.getLocation();
                        userName.setText(user.getName());
                        phoneNum.setText(user.getPhoneNum());
                        if (userLocation!=null){
                            location.setText(userLocation.getProvince()
                                    + userLocation.getCity()
                                    + userLocation.getStreet());
                        }
                        Picasso.get().load(user.getPicLocation()).into(userPic);
                        break;
                    case 1:
                        Product product = (Product)msg.obj;
                        if (product.getPictures()!=null && !product.getPictures().isEmpty()){
                            Picasso.get().load(product.getPictures().get(0).getLocation()).into(firstProductImage);
                        }else {
                            Picasso.get().load(R.drawable.add).into(firstProductImage);
                        }
                        productName.setText(product.getProductName());
                        productAmount.setText(product.getProductAmount());
                        productPrice.setText(product.getProductPrice());
                        ProductDetailAdapter adapter = new ProductDetailAdapter(product.getPictures(),ProductInfoActivity.this);
                        recyclerView.setAdapter(adapter);
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
                            Message message = dataHandler.obtainMessage();
                            message.obj = output;
                            message.what = 0;
                            dataHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();

    }

    public void getProduct(final int id){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/product/findById/" + id).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("message","Fail to connect!");
                            //Toast.makeText(getContext(),"连接服务器失败！",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.e(OKHTTP_MESSAGE,response.body().string());
                            Product output = JSON.parseObject(response.body().string(), new TypeReference<Product>(){});
                            Message message = dataHandler.obtainMessage();
                            message.obj = output;
                            message.what = 1;
                            dataHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();

    }



}
