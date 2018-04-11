package com.example.farmerqi.farm.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmerqi.farm.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by FarmerQi on 2018/4/8.
 */

public class GridViewAdapter extends BaseAdapter {
    List<Uri> input;
    ImageView imageView;
    TextView textView;
    LayoutInflater layoutInflater;
    Context context;

    public GridViewAdapter(Context context,List<Uri> input){
        this.context = context;
        this.input = input;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //设置多一个位置，为添加图片按钮提供位置
        //在这里如果输入的list为空，需要将数目设置为1，为占位图片提供位置。
        if (input == null){
            return 1;
        }
        return input.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return input.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.upload_gridview_item,null);
        imageView = (ImageView)convertView.findViewById(R.id.grid_view_item_image);
        //处理初始化的问题，在LIST为空的情况下需要针对特殊情况进行处理
        if (input == null){
            imageView.setBackgroundResource(R.drawable.add);
        }else if (position < input.size()){
            Picasso.get().load(input.get(position)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }else {

            imageView.setBackgroundResource(R.drawable.add);
            imageView.setScaleType(ImageView.ScaleType.CENTER);

        }
        return convertView;
    }
}
