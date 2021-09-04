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
import com.example.assets.Model.ChangePasswordRequest;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Custom_Dialog_Changpass extends AppCompatDialogFragment {
    TextInputEditText newPassword,oldPassword;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_change_pass, null);

        newPassword = view.findViewById(R.id.newPassword);
        oldPassword = view.findViewById(R.id.oldPassword);
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
        if(d!=null)
        {
            Button button = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(oldPassword.getText().toString().equals(newPassword.getText().toString()))
                    {
                        Toast.makeText(view.getContext(), "New and old password must be different", Toast.LENGTH_SHORT).show();
                    }else {
                        if (oldPassword.getText().toString().length() < 8 || oldPassword.getText().toString().length() > 36) {
                            Toast.makeText(view.getContext(), "Old password must be between 8 and 36", Toast.LENGTH_SHORT).show();
                        } else {
                            if (newPassword.getText().toString().length() < 8 || newPassword.getText().toString().length() > 36) {
                                Toast.makeText(view.getContext(), "New password must be between 8 and 36", Toast.LENGTH_SHORT).show();
                            } else {
                                ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
                                changePasswordRequest.setNewPassword(newPassword.getText().toString());
                                changePasswordRequest.setOldPassword(oldPassword.getText().toString());
                                MainActivity.service.changePass(changePasswordRequest).enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        if (response.code() == 200) {
                                            if (response.body() == true) {
                                                Toast.makeText(view.getContext(), "Change password success", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(view.getContext(), "Your old password not correct", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(view.getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
                                        }
                                        dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        Toast.makeText(view.getContext(), "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }
}
