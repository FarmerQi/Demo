package com.example.farmerqi.farm.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerqi.farm.R;
import com.example.farmerqi.farm.model.User;
import com.google.gson.Gson;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by FarmerQi on 2018/1/27.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String MESSAGE_TAG = "RESPONSE_RESULT";
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText phoneNumber;
    private Button submitButton;
    private TextView registerTextView;
    private TextView toMainText;

//    private LoginHandler loginHandler = new LoginHandler(this);
//    private static class LoginHandler extends Handler{
//        private WeakReference<LoginActivity> weakReference;
//
//        public LoginHandler(LoginActivity loginActivity){
//            weakReference = new WeakReference<LoginActivity>(loginActivity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            final LoginActivity loginActivity = (LoginActivity)weakReference.get();
//            if (loginActivity == null){
//                return;
//            }
//            String result = (String)msg.obj;
//            Log.e("handler",result);
//
//        }
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        accountEditText = (EditText)findViewById(R.id.account_edit_text);
        passwordEditText = (EditText)findViewById(R.id.password_edit_text);
        submitButton = (Button)findViewById(R.id.login_button);
        submitButton.setOnClickListener(this);
        registerTextView = (TextView)findViewById(R.id.register_text_view);
        registerTextView.setOnClickListener(this);

        toMainText = (TextView)findViewById(R.id.toMainText);
        toMainText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        toMainText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                InputMethodManager inputManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(passwordEditText.getWindowToken(),0);
                //初始化对象并发送用户信息验证请求
                String account = accountEditText.getText().toString();
                Log.e("Account",account);
                String password = passwordEditText.getText().toString();
                Log.e("Psw",password);
                login(account,password);
                break;
            case R.id.register_text_view:

                break;
            case R.id.toMainText:
                Intent toMain = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(toMain);
                finish();
                break;
        }
    }

    public void login(final String account,final String psw){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    FormBody formBody = new FormBody.Builder().add("account",account).add("psw",psw).build();
                    final Request request = new Request.Builder().url("http://192.168.191.1:8080/user/login").post(formBody).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("FAIL","登录失败！");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String result = response.body().string();
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this,result,Toast.LENGTH_SHORT).show();

                                    Intent toMain = new Intent(LoginActivity.this,MainActivity.class);

                                    startActivity(toMain);
                                    finish();
                                }
                            });


                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }).start();

    }
}
