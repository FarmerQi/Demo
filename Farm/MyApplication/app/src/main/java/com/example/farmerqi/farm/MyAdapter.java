package com.example.farmerqi.farm;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmerqi.farm.MainActivity;
import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.model.Picture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FarmerQi on 2018/1/8.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    private List<Picture> dates = null;

    public MyAdapter(List<Picture> dates ){
        this.dates = dates;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_recyclerview,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(dates.get(position).getName());
        Picasso.get().load(dates.get(position).getLocation()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ImageView imageView;
        public MyViewHolder(View view){
            super(view);
            textView = (TextView) view.findViewById(R.id.card_view_text);
            imageView = (ImageView) view.findViewById(R.id.cardview_image);
        }
    }
}
