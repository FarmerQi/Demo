package com.example.farmerqi.farm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.model.Picture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by FarmerQi on 2018/5/21.
 */

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.MyViewHolder> {
    List<Picture> pictures = new ArrayList<>();
    Context context;

    public ProductDetailAdapter(List<Picture> pictures,Context context){
        this.pictures = pictures;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_detail_recycler_view_item,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (pictures == null || pictures.isEmpty()){
            Picasso.get().load(R.drawable.add).into(holder.imageView);
        }else {
            Picture temp = pictures.get(position);
            Picasso.get().load(temp.getLocation()).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (pictures==null || pictures.isEmpty()){
            return 1;
        }
        return pictures.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.product_pic);
        }
    }
}
