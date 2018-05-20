package com.example.farmerqi.farm.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.activity.ProductInfoActivity;
import com.example.farmerqi.farm.activity.UserInfoActivity;
import com.example.farmerqi.farm.model.Location;
import com.example.farmerqi.farm.model.Picture;
import com.example.farmerqi.farm.model.Product;
import com.example.farmerqi.farm.model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by FarmerQi on 2018/1/8.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
    List<Product> data = new ArrayList<>();
    Context context ;

    public MyAdapter(List<Product> data,Context context){
        this.data = data;
        this.context = context;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_recyclerview,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

       if (data==null || data.isEmpty()){
           holder.productNameTextView.setText("空");
       }else {
           Location location = data.get(position).getLocation();
           final User user = data.get(position).getUser();
           List<Picture> pictures = data.get(position).getPictures();

           //设置产品名称
           holder.productNameTextView.setText(data.get(position).getProductName() + data.get(position).getProductID());
           //设置产品价格
           holder.productPriceTextView.setText(data.get(position).getProductPrice());

           if (location != null){
               //设置地理位置
               holder.productLocationTextView.setText(location.getProvince() + location.getCity() + location.getStreet());
           }else {
               holder.productLocationTextView.setText("");
           }

           //设置图片
           if (!pictures.isEmpty()){
               Picasso.get().load(pictures.get(0).getLocation()).into(holder.imageView);

           }else {
               Picasso.get().load(R.drawable.buytag).into(holder.imageView);
           }
           if (user.getPicLocation()!=null){
               Picasso.get().load(user.getPicLocation()).into(holder.userImage);
           }else {
               Picasso.get().load(R.drawable.add).into(holder.userImage);
           }

           //产品图片的点击事件
           holder.imageView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent toProductInfoActivity = new Intent(context, ProductInfoActivity.class);
                   toProductInfoActivity.putExtra("productID",data.get(position).getProductID());
                   toProductInfoActivity.putExtra("userID",user.getUserID());
                   context.startActivity(toProductInfoActivity);
               }
           });
           //用户头像的点击事件
           holder.userImage.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent toUserInfoActivity = new Intent(context, UserInfoActivity.class);
                   toUserInfoActivity.putExtra("userID",user.getUserID());
                   context.startActivity(toUserInfoActivity);
               }
           });
       }


    }

    @Override
    public int getItemCount() {
        if (data == null || data.isEmpty()){
            return 1;
        }
        return data.size();

    }
    class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView productNameTextView;
        public TextView productLocationTextView;
        public TextView productPriceTextView;
        public ImageView imageView;
        public CircleImageView userImage;
        public MyViewHolder(View view){
            super(view);
            productNameTextView = (TextView)view.findViewById(R.id.product_name);
            productLocationTextView = (TextView)view.findViewById(R.id.product_location);
            productPriceTextView = (TextView)view.findViewById(R.id.product_price);
            imageView = (ImageView) view.findViewById(R.id.cardview_image);
            userImage = (CircleImageView)view.findViewById(R.id.user_pic);
        }
    }
}
