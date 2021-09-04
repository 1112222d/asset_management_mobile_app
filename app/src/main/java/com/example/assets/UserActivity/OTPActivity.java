package com.example.assets.UserActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.R;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity {
    PinView pinView;
    Button confirm, resender;
    TextView text_mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        pinView = findViewById(R.id.pin_view);
        confirm = findViewById(R.id.btn_verify);
        resender = findViewById(R.id.btn_resentOTP);
        text_mail = findViewById(R.id.text_mail);
        text_mail.setText("Your OTP has been sent to mail: \n" + MainActivity.email);
        resender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("email", MainActivity.email);

                MainActivity.service.getOtp(map).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.code() == 200) {
                            Toast.makeText(OTPActivity.this, "Resend successfully", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Toast.makeText(OTPActivity.this, "Error : Something went wrong  ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("email", MainActivity.email);
                MainActivity.service.forgotPassword(map, Integer.parseInt(pinView.getText().toString())).enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.code() == 200) {
                            MessageDialog.getInstance(OTPActivity.this, "Success", response.body().get("message")).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();

                        }
                        if (response.code() == 400) {
                            MessageDialog.getInstance(OTPActivity.this, "Error", "Please try again").setPositiveButton("OK", null).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {

                    }
                });

            }
        });
    }
}