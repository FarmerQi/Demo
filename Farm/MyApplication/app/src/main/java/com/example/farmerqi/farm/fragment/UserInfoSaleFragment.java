package com.example.farmerqi.farm.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.MyAdapter;
import com.example.farmerqi.farm.model.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2018/5/20.
 */

public class UserInfoSaleFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textView;
    private getSaleProducts getSaleProducts;
    private List<Product> temp;

    private Handler productsHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getSaleProducts = (getSaleProducts)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info_sale,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.user_info_sale_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(10);

        Log.e("111111",getArguments().getInt("user") + "");
        getProducts(getArguments().getInt("user"));

        productsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        List<Product> products = (List<Product>) msg.obj;
                        Log.e("2222222",products.size() + "");
                        List<Product> buyProducts = new ArrayList<>();
                        for (Product product: products) {
                            if (product.getTag().equals("")){
                                buyProducts.add(product);
                            }
                        }
                        Log.e("333333333333",buyProducts.size() + "");
                        MyAdapter myAdapter = new MyAdapter(buyProducts,getActivity());
                        recyclerView.setAdapter(myAdapter);
                }
            }
        };


        //recyclerView.setNestedScrollingEnabled(false);
        textView = (TextView)view.findViewById(R.id.user_info_sale_text);
        return view;
    }

    public interface getSaleProducts{
        int getSaleProducts();
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
