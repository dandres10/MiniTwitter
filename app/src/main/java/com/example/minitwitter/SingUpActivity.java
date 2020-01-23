package com.example.minitwitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SingUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp;
    TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        getSupportActionBar().hide();

        btnSignUp = findViewById(R.id.buttonSignUp);
        tvGoToLogin = findViewById(R.id.textViewGoLogin);


        btnSignUp.setOnClickListener(this);
        tvGoToLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonSignUp:
                break;
            case R.id.textViewGoLogin:
                goToLogin();
                break;


        }
    }

    private void goToLogin() {
        Intent i = new Intent(SingUpActivity.this,MainActivity.class);
        startActivity(i);
        finish();

    }
}
