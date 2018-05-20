package com.example.farmerqi.farm.fragment;

import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.activity.MainActivity;
import com.example.farmerqi.farm.adapter.MyAdapter;
import com.example.farmerqi.farm.model.Picture;
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
 * Created by FarmerQi on 2018/3/13.
 */

public class BuyFragment extends Fragment implements View.OnClickListener{
    private static final String OKHTTP_MESSAGE = "the net state .......";
    private RecyclerView buyPageRecyclerView;
    private List<Product> output = new ArrayList<>();
    private Button sendButton;
    private MyAdapter myAdapter;

    private List<Drawable> productsPic = null;
    private List<String> productMessages = null;
    private List<Drawable> userPics = null;

    private Handler productHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getProducts();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy,container,false);
        buyPageRecyclerView = (RecyclerView)view.findViewById(R.id.buy_fragment_recyclerView);
        buyPageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        buyPageRecyclerView.setHasFixedSize(false);
        buyPageRecyclerView.setItemViewCacheSize(10);
        productHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<Product> products = new ArrayList<>();
                products = (List<Product>) msg.obj;
                MyAdapter myAdapter= new MyAdapter(products, getActivity());
                buyPageRecyclerView.setAdapter(myAdapter);

            }
        };
//        sendButton = (Button)view.findViewById(R.id.send_button_buy_fragment);
//        sendButton.setOnClickListener(this);
//
//        productsPic = new ArrayList<>();
//        productsPic.add(getResources().getDrawable(R.drawable.shengcai));
//        productsPic.add(getResources().getDrawable(R.drawable.fanqie));
//        productsPic.add(getResources().getDrawable(R.drawable.xigua));
//
//        productMessages = new ArrayList<>();
//        productMessages.add("商品名称：生菜 \n价格：2 元/斤 \n数量：100公斤 " );
//        productMessages.add("商品名称：番茄 \n价格：3 元/斤 \n数量：200公斤 " );
//        productMessages.add("商品名称：西瓜 \n价格：2.5 元/斤 \n数量：800公斤 " );
//
//        userPics = new ArrayList<>();
//        userPics.add(getResources().getDrawable(R.drawable.userimage));
//        userPics.add(getResources().getDrawable(R.drawable.userimage));
//        userPics.add(getResources().getDrawable(R.drawable.userimage));


        return view;
    }



    public List<Product> getProducts(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/product/findAll").build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(OKHTTP_MESSAGE,"Fail to connect!");
                            //Toast.makeText(getContext(),"连接服务器失败！",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //Log.e(OKHTTP_MESSAGE,response.body().string());
                            output = JSON.parseObject(response.body().string(), new TypeReference<List<Product>>(){});
                            Message message = productHandler.obtainMessage();
                            message.obj = output;
                            productHandler.sendMessage(message);
                        }
                    });
                }catch (Exception e){

                }
            }
        }).start();
        return output;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.send_button_buy_fragment:
//                if (getPic()==null){
//                    Toast.makeText(getContext(),"未获取到数据",Toast.LENGTH_LONG).show();
//                }else {
////                    myAdapter =  new MyAdapter(getPic());
////                    buyPageRecyclerView.setAdapter(myAdapter);
//                }
//                break;
        }
    }
}
