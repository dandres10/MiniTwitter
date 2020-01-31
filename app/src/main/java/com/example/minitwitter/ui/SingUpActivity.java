package com.example.minitwitter.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.MiniTwitterClient;
import com.example.minitwitter.retrofit.MiniTwitterService;
import com.example.minitwitter.retrofit.request.RequestSignup;
import com.example.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSignUp;
    TextView tvGoToLogin;
    EditText etUsername, etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        getSupportActionBar().hide();
        retrofitInit();
        findViews();
        events();


    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();
    }


    private void findViews() {
        btnSignUp = findViewById(R.id.buttonSignUp);
        tvGoToLogin = findViewById(R.id.textViewGoLogin);
        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }

    private void events() {
        btnSignUp.setOnClickListener(this);
        tvGoToLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonSignUp:
                goToSignUp();
                break;
            case R.id.textViewGoLogin:
                goToLogin();
                break;


        }
    }

    private void goToSignUp() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es requerido");
        } else if (email.isEmpty()) {
            etEmail.setError("El email es requerido");
        } else if (password.isEmpty() || password.length() < 4) {
            etPassword.setError("El password es requerido y debe tener 4 caracteres");
        } else {
            RequestSignup requestSignup = new RequestSignup(username, email, password, Constantes.CODIGO_SIGNUP);
            Call<ResponseAuth> call = miniTwitterService.doSignUp(requestSignup);
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if (response.isSuccessful()) {
                        storeVariablesResponse(response);
                        Intent i = new Intent(SingUpActivity.this, DashboardActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(SingUpActivity.this, "Algo ha ido mal, revise los datos de registro", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(SingUpActivity.this, "Error la conexión", Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private void goToLogin() {
        Intent i = new Intent(SingUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();

    }

    private void storeVariablesResponse(Response<ResponseAuth> response) {
        SharedPreferencesManager
                .setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
        SharedPreferencesManager
                .setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
        SharedPreferencesManager
                .setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
        SharedPreferencesManager
                .setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
        SharedPreferencesManager
                .setSomeStringValue(Constantes.PREF_CREATED, response.body().getCreated());
        SharedPreferencesManager
                .setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());
    }

}
