package com.example.farmerqi.farm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.cloud.CloudItem;
import com.example.farmerqi.farm.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by FarmerQi on 2018/4/19.
 */

public class LocationResultAdapter extends RecyclerView.Adapter<LocationResultAdapter.LocationResultViewHolder> {
    private List<CloudItem> input;
    public LocationResultAdapter(List<CloudItem> input){
        this.input = input;
    }
    @Override
    public LocationResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_search_result_item,null);
        LocationResultViewHolder viewHolder = new LocationResultViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(LocationResultViewHolder holder, int position) {
        if (input == null || input.isEmpty()){
            holder.resultText.setText("没有搜索到");
            holder.resultImage.setVisibility(View.GONE);
        }else {
            holder.resultText.setText(input.get(position).getTitle() + "\n"+input.get(position).getSnippet());
            Picasso.get().load(input.get(position).getCustomfield().get("user_pic")).into(holder.resultImage);
            //holder.resultImage.setBackgroundResource(R.drawable.placeholderpic);
        }

    }

    @Override
    public int getItemCount() {
        if (input == null || input.isEmpty()){
            return 1;
        }
        return input.size();
    }
    class LocationResultViewHolder extends RecyclerView.ViewHolder{
        public TextView resultText;
        public ImageView resultImage;
        public LocationResultViewHolder(View view){
            super(view);
            resultText = (TextView)view.findViewById(R.id.location_search_result_item_text_view);
            resultImage = (ImageView)view.findViewById(R.id.location_search_result_item_image_view);
        }
    }
}
