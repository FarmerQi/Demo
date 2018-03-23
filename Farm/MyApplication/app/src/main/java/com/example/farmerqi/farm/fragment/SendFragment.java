package com.example.farmerqi.farm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.farmerqi.farm.R;

/**
 * Created by FarmerQi on 2018/3/23.
 */

public class SendFragment extends Fragment implements View.OnClickListener{

    private TextView demoText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send,container,false);
        demoText = (TextView)view.findViewById(R.id.send_fragment_text);
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
