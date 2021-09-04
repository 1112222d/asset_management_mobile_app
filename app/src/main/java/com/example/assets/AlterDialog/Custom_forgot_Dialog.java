package com.example.assets.AlterDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;


import com.example.assets.MainActivity;
import com.example.assets.R;
import com.example.assets.RetrofitClass;
import com.example.assets.UserActivity.OTPActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Custom_forgot_Dialog extends AppCompatDialogFragment {
    TextInputEditText fg_username;
    Intent intent;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_forgot_pass, null);

        fg_username = view.findViewById(R.id.fg_username);

        builder.setView(view).setTitle("Email :").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                    Map<String, String> map = new HashMap<>();
                    map.put("email", fg_username.getText().toString());

                    MainActivity.service.getOtp(map).enqueue(new Callback<Map<String, String>>() {
                        @Override
                        public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                            if (response.code() == 200) {
                                Map<String, String> mapResult = new HashMap<>();
                                mapResult.put("status", response.body().get("status"));
                                mapResult.put("message", response.body().get("message"));
                                MainActivity.OTP.setValue(mapResult);
                                MainActivity.email = fg_username.getText().toString();

                            } else {
                                Map<String, String> mapResult = new HashMap<>();
                                mapResult.put("status", "false");
                                mapResult.put("message", "Account not found");
                                MainActivity.OTP.setValue(mapResult);
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, String>> call, Throwable t) {

                        }
                    });
                }




        });


        return builder.create();
    }

}
