package com.example.assets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;


import com.example.assets.AdminActivity.AdminActivity;
import com.example.assets.AlterDialog.Custom_forgot_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.Model.LoginRequest;
import com.example.assets.Model.LoginResponse;
import com.example.assets.UserActivity.OTPActivity;
import com.example.assets.UserActivity.UserActivity;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static Service service = RetrofitClass.getInstance().create(Service.class);
    public static LoginResponse loginResponse;
    public static MutableLiveData<Map<String,String>> OTP=new MutableLiveData<>();
    public static String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextInputEditText username =findViewById(R.id.et_username);
        EditText password = findViewById((R.id.et_password));
        Button signIn =findViewById(R.id.button_signin);
        Button forgotPassword = findViewById(R.id.button_forgot_password);
        RelativeLayout progressBar = findViewById(R.id.prgrsbar);
        MainActivity.OTP.observe(MainActivity.this, new Observer<Map<String,String>>() {
            @Override
            public void onChanged(Map<String,String> map) {
                if(map.get("status").equals("true"))
                {
                    Intent intent = new Intent(MainActivity.this, OTPActivity.class);
                    startActivity(intent);
                }else
                {
                    MessageDialog.getInstance(MainActivity.this,"Error","Account not found").show();

                }
            }
        });
        signIn.setOnClickListener(v -> {
            if(!username.getText().toString().trim().equals("")&&!password.getText().toString().trim().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                service.login(new LoginRequest(username.getText().toString().trim(), password.getText().toString().trim()))
                        .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        int code = response.code();
                        if (code == 200) {
                            if (response.body() != null) {
                                Intent intent;
                                loginResponse = response.body();
                                username.setText("");
                                password.setText("");
                                service = RetrofitClass.getInstance(response.body().getToken()).create(Service.class);
                                if (loginResponse.getRole().equals("ROLE_ADMIN")) {

                                    intent = new Intent(MainActivity.this, AdminActivity.class);
                                } else {
                                    intent = new Intent(MainActivity.this, UserActivity.class);
                                }
                                startActivity(intent);
                            }
                        } else {
                            if(code==409)
                            {
                                MessageDialog.getInstance(MainActivity.this, "Error", "Account is disabled").show();
                            }else
                            if (code == 404 || code == 401) {
                                MessageDialog.getInstance(MainActivity.this, "Error", "Username or password is incorrect. Please try again").show();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        MessageDialog.getInstance(MainActivity.this, "Error",
                                "Something went wrong").show();
                    }
                });
            }else{
                MessageDialog.getInstance(MainActivity.this, "Not blank",
                        "Username and password has been blank").show();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }
    public void openDialog()
    {
        Custom_forgot_Dialog custom_forgot_dialog = new Custom_forgot_Dialog();
        custom_forgot_dialog.show(getSupportFragmentManager(),"Forgot password");
    }

}