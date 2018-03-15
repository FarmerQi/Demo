package com.example.farmerqi.farm.fragment;

import android.os.Bundle;
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
import com.example.farmerqi.farm.adapter.MyAdapter;
import com.example.farmerqi.farm.model.Picture;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2018/3/13.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{
    private static final String OKHTTP_MESSAGE = "the net state .......";
    private RecyclerView homePageRecyclerView;
    private List<Picture> output;
    private Button sendButton;
    private MyAdapter myAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first,container,false);
        homePageRecyclerView = (RecyclerView)view.findViewById(R.id.first_fragment_recyclerView);
        homePageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        homePageRecyclerView.setHasFixedSize(false);
        homePageRecyclerView.setItemViewCacheSize(10);

        sendButton = (Button)view.findViewById(R.id.send_button_first_fragment);
        sendButton.setOnClickListener(this);
        if (getPic()==null){
            Toast.makeText(getContext(),"未获取到数据",Toast.LENGTH_LONG).show();
        }else {
            myAdapter =  new MyAdapter(getPic());
            homePageRecyclerView.setAdapter(myAdapter);
        }
        return view;
    }



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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_button_first_fragment:
                if (getPic()==null){
                    Toast.makeText(getContext(),"未获取到数据",Toast.LENGTH_LONG).show();
                }else {
                    myAdapter =  new MyAdapter(getPic());
                    homePageRecyclerView.setAdapter(myAdapter);
                }

                break;
        }
    }
}
