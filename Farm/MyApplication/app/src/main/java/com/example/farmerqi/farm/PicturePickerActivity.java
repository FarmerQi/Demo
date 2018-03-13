package com.example.farmerqi.farm;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;



/**
 * Created by FarmerQi on 2018/3/12.
 */
@RuntimePermissions
public class PicturePickerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String RESULT = "URI_MESSAGE.........";
    Button pickButton;
    RecyclerView pickRecyclerView;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_picker_layout);
        pickButton = (Button)findViewById(R.id.picture_pick_button);
        pickRecyclerView = ( RecyclerView)findViewById(R.id.picture_picker_recyclerView);
        pickRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pickRecyclerView.setHasFixedSize(false);
        pickRecyclerView.setItemViewCacheSize(10);

        //由PermissionDispatcher封装后的方法实现点击事件
        pickButton.setOnClickListener(this);



    }

    //获取权限后的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PicturePickerActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    //点击函数接口实现
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.picture_pick_button:
                PicturePickerActivityPermissionsDispatcher.getPicWithPermissionCheck(this);
        }
    }
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void getPic(){
        /*
            使用Matisse作为图片加载框架时，使用Glide作为图片加载引擎的话，由于Glide版本的原因会导致
            各种报错，再加上知乎团队很久没有维护该框架，导致无法适配最新版本的Glide，因此解决方案可以
            是：使用低版本的Glide或者使用最新版本的Glide，之后再改写GlideEngngine的一些内部方法。
            在这里选择使用老版本的Glide来进行开发
             */
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(10)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            List<Uri> result = Matisse.obtainResult(data);
            Log.e(RESULT,result.toString());
            MyAdapter myAdapter = new MyAdapter(result);
            pickRecyclerView.setAdapter(myAdapter);
        }
    }

    //配置RecyclerView的组件
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        private List<Uri> datas = null;
        public MyAdapter(List<Uri> datas){
            this.datas = datas;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_recyclerview,parent,false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.textView.setText(datas.get(position).toString());
            Picasso.get().load(datas.get(position)).into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView textView;
            private ImageView imageView;
            public MyViewHolder(View itemView) {
                super(itemView);
                textView = (TextView)itemView.findViewById(R.id.card_view_text);
                imageView = (ImageView)itemView.findViewById(R.id.cardview_image);
            }
        }
    }
}
