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
import com.example.minitwitter.retrofit.MiniTwitterClient;
import com.example.minitwitter.retrofit.MiniTwitterService;
import com.example.minitwitter.retrofit.request.RequestLogin;
import com.example.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogin;
    TextView tvGoSingUp;
    EditText etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        retrofitInit();
        findViews();
        events();

        btnLogin.setOnClickListener(this);
        tvGoSingUp.setOnClickListener(this);

    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();

    }


    private void findViews() {
        btnLogin = findViewById(R.id.buttonLogin);
        tvGoSingUp = findViewById(R.id.textViewGoSignUp);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }

    private void events() {
        btnLogin.setOnClickListener(this);
        tvGoSingUp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.buttonLogin:
                goToLogin();
                break;
            case R.id.textViewGoSignUp:
                goToSingUp();
                break;
        }
    }

    private void goToLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.isEmpty()){
            etEmail.setError("El email es requerido.");
        }else if(password.isEmpty()){
                    etPassword.setError("La contraseña es requerida");
        }else{
            RequestLogin requestLogin = new RequestLogin(email,password);
            Call<ResponseAuth> call = miniTwitterService.doLogin(requestLogin);
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Sesion iniciada correctamente.",Toast.LENGTH_LONG);
                            Intent i = new Intent(MainActivity.this,DashboardActivity.class);
                            startActivity(i);

                            //destruyendo este activity para no poder volver
                            finish();
                        }else{
                                Toast.makeText(MainActivity.this,"Algo fue mal rebice sus datos de acceso.",Toast.LENGTH_LONG);
                        }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(MainActivity.this,"Problemas de conexion",Toast.LENGTH_LONG);
                }
            });


        }

    }

    private void goToSingUp() {
        Intent i = new Intent(MainActivity.this, SingUpActivity.class);
        startActivity(i);
        finish();
    }
}
