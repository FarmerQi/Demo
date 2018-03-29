package com.example.farmerqi.farm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.farmerqi.farm.R;

/**
 * Created by FarmerQi on 2018/3/23.
 */

public class ReleaseFragment extends Fragment implements View.OnClickListener{

    private TextView demoText;
    private PopupWindow popupWindow;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_release,container,false);
        demoText = (TextView)view.findViewById(R.id.send_fragment_text);
        showPopupWindow();
        demoText.setOnClickListener(this);
        return view;
    }

    private void showPopupWindow(){
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.cardview_popup,null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        popupWindow.setContentView(contentView);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.nav_bar_main,null);
        //设置popupwidow与某一控件的显示位置
        popupWindow.showAtLocation(rootView, Gravity.TOP,0,0);
    }

    @Override
    public void onClick(View v) {
        showPopupWindow();
    }
}
