package com.example.farmerqi.farm.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.adapter.GridViewAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * Created by FarmerQi on 2018/4/8.
 */
@RuntimePermissions
public class UpLoadActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_CHOOSE = 23;


    private EditText uploadEditText;
    private GridView uploadGridView;
    private GridViewAdapter gridViewAdapter;
    private Button selectButton;


    private List<Uri> list;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);
        uploadEditText = (EditText)findViewById(R.id.introduce_edit_text);
        uploadGridView = (GridView)findViewById(R.id.image_grid_view);
        selectButton = (Button)findViewById(R.id.test);
        selectButton.setOnClickListener(this);
        uploadGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getChildCount() - 1){
                    UpLoadActivityPermissionsDispatcher.getPicWithPermissionCheck(UpLoadActivity.this);
                }else {
                    Toast.makeText(UpLoadActivity.this,"这是第 " + position + "张图片",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test:
                UpLoadActivityPermissionsDispatcher.getPicWithPermissionCheck(this);
        }
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void getPic(){
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(10)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UpLoadActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            list = Matisse.obtainResult(data);
            gridViewAdapter = new GridViewAdapter(UpLoadActivity.this,list);
            uploadGridView.setAdapter(gridViewAdapter);
            gridViewAdapter.notifyDataSetChanged();
        }
    }
}
