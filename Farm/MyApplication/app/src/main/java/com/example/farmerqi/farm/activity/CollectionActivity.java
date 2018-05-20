package com.example.farmerqi.farm.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.MyAdapter;
import com.example.farmerqi.farm.model.Product;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2018/5/20.
 */

public class CollectionActivity extends AppCompatActivity {
    private Handler productsHandler;
    private RecyclerView productsRecyclerView;
    private ImageView back;
    private TextView title;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        title = (TextView)findViewById(R.id.release_activity_title);
        title.setText("已收藏");
        getProducts(5);


        back = (ImageView)findViewById(R.id.release_activity_back_image);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        productsRecyclerView = (RecyclerView)findViewById(R.id.release_activity_recycler_view);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        productsRecyclerView.setHasFixedSize(false);
        productsRecyclerView.setItemViewCacheSize(10);
        productsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        List<Product> products = (List<Product>) msg.obj;
                        MyAdapter myAdapter = new MyAdapter(products,CollectionActivity.this);
                        productsRecyclerView.setAdapter(myAdapter);
                }
            }
        };

    }

    public void getProducts(final int id){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/product/findByUserId/" + id).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("message","Fail to connect!");
                            //Toast.makeText(getContext(),"连接服务器失败！",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.e(OKHTTP_MESSAGE,response.body().string());
                            List<Product> products = JSON.parseObject(response.body().string(), new TypeReference<List<Product>>(){});
                            Message message = productsHandler.obtainMessage();
                            message.obj = products;
                            message.what = 1;
                            productsHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();

    }

}
