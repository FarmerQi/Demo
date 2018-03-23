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

public class MessageFragment extends Fragment {
    private TextView messageFragmentText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message,container,false);
        messageFragmentText = (TextView)view.findViewById(R.id.message_fragment_text);

        return view;
    }
}
