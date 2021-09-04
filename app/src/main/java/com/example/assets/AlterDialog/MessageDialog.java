package com.example.assets.AlterDialog;

import android.content.Context;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MessageDialog {
    public static MaterialAlertDialogBuilder materialAlertDialogBuilder;
    public static MaterialAlertDialogBuilder getInstance(Context context , String title, String message)
    {
        materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message).setPositiveButton("OK",(dialog, which) -> {

        });
        return materialAlertDialogBuilder;
    }

}
