package com.example.farmerqi.farm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by FarmerQi on 2018/1/27.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

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

                break;
            case R.id.register_text_view:

                break;
            default:
                    break;
        }
    }
}
