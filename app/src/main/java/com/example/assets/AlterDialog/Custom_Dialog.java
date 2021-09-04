package com.example.assets.AlterDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.example.assets.MainActivity;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.textfield.TextInputEditText;


import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Custom_Dialog extends AppCompatDialogFragment {
    TextInputEditText newPassword;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_first_login, null);

        newPassword = view.findViewById(R.id.newPassword);
        builder.setView(view).setTitle("Change password :").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }
    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d= (AlertDialog)getDialog();
        if(d!=null) {
            Button button = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(newPassword.getText().toString().length()<8||newPassword.getText().length()>36)
                    {
                        Toast.makeText(v.getContext(), "New password must be between 8 and 36", Toast.LENGTH_SHORT).show();
                    }else {
                        Map<String,String> map = new HashMap<>();
                        map.put("password",newPassword.getText().toString().trim());
                        MainActivity.service.firstLogin(map).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(response.code()==200)
                                {
                                    MainActivity.loginResponse.setFirstLogin(false);
                                    Toast.makeText(v.getContext(), "Change password success", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }else
                                {
                                    Toast.makeText(v.getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(v.getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }
        }
}
