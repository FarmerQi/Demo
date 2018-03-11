package com.example.farmerqi.farm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by FarmerQi on 2018/3/5.
 */

public class PicCompressActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView resourcePic;
    ImageView compressedPic;
    TextView sourceText;
    TextView compressedText;
    Button compressButton;
    private static final String COMPRESS_START = "开始压缩图片";
    private static final String COMPRESSING = "正在压缩";
    private static final String COMPRESS_RESULT = "压缩完成";
    File input;
    File output;
    Bitmap sourceBitmap;
    Bitmap compressedBitmap;
//    private Drawable sourceDrawable;
//    private Drawable compressDrawable;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_compress_layout);
        resourcePic = (ImageView)findViewById(R.id.source_pic);
        compressedPic = (ImageView)findViewById(R.id.compressed_pic);
        sourceText = (TextView)findViewById(R.id.source_size);
        compressedText = (TextView)findViewById(R.id.compressed_size);
        compressButton = (Button)findViewById(R.id.compress_button);
        resourcePic.setImageDrawable(getResources().getDrawable(R.drawable.bg_2018));
        sourceBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bg_2018);
        input = saveBitmap(sourceBitmap);
        compressButton.setOnClickListener(this);
    }
    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.compress_button:

                //compressPic(input);
                //getSize();
                Picasso.get().load("http://192.168.191.1:8080/46201.jpg").into(compressedPic);
        }
    }

//    public void getSize(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Long sourceSize = input.getTotalSpace();
//                Long compressSize = output.getTotalSpace();
//                sourceText.setText(sourceSize.toString());
//                compressedText.setText(compressSize.toString());
//                compressedBitmap = BitmapFactory.decodeFile(output.getPath());
//                compressedPic.setImageBitmap(compressedBitmap);
//
//            }
//        });
//    }
    public void compressPic(final File input){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Luban.with(getApplicationContext())
                        .load(input)
                        .ignoreBy(100)
                        .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        Log.e(COMPRESS_START,"Start compress!!!");
                        Log.e(COMPRESSING,"Compressing....");
                    }

                    @Override
                    public void onSuccess(File file) {
                        Log.e(COMPRESS_RESULT,"Finish");
                        Log.e(COMPRESS_RESULT,file.getPath());
                        output = new File(file.getPath());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                }).launch();
            }
        }).start();
    }

    public File saveBitmap(Bitmap bitmap){
        String path = Environment.getExternalStorageState();
        File file = new File(path+"/test.png");
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG,100,bufferedOutputStream);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

}
