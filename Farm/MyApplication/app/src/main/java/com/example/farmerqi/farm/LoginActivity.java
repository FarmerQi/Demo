package com.example.farmerqi.farm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farmerqi.farm.model.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
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
    private Button submitButton;
    private TextView registerTextView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        accountEditText = (EditText)findViewById(R.id.account_edit_text);
        passwordEditText = (EditText)findViewById(R.id.password_edit_text);
        submitButton = (Button)findViewById(R.id.login_button);
        registerTextView = (TextView)findViewById(R.id.register_text_view);

        submitButton.setOnClickListener(this);
        registerTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                //初始化对象并发送用户信息验证请求
                String username = accountEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                User user = new User();
                user.setName(username);
                user.setPassword(password);
                Gson input = new Gson();
                String inputString = input.toJson(user);
                login(inputString);
                break;
            case R.id.register_text_view:

                break;
            default:
                    break;
        }
    }

    public void login(final String input){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS).build();
                    RequestBody requestBody = FormBody.create(MediaType.parse("application/json;charset=utf-8"),input);
                    Request request = new Request.Builder().url("http://farmerqi.imwork.net/user/login").post(requestBody).build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.i(MESSAGE_TAG,"*****");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.i(MESSAGE_TAG,"GET RESPONSE");
                            Toast.makeText(LoginActivity.this,response.body().toString(),Toast.LENGTH_LONG).show();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }
}
