package com.example.assets.UserActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.example.assets.AdminActivity.AdminActivity;
import com.example.assets.AlterDialog.Custom_Dialog;
import com.example.assets.AlterDialog.Custom_Dialog_Changpass;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class UserActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    LinearLayout myAssignment, userRequest, logout;
    RoundedImageView user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        myAssignment = findViewById(R.id.myAssignment);
//        userRequest=findViewById(R.id.userRequest);
        logout = findViewById(R.id.logout);
        user = findViewById(R.id.imageProfileComment);
        myAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.loginResponse.getFirstLogin()) {
                    openDialog();
                } else {
                    Intent intent = new Intent(UserActivity.this, MyAssignmentActivity.class);
                    startActivity(intent);
                }
            }
        });
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.loginResponse.getFirstLogin()) {
                    openDialog();
                } else {
                    showMenu(v);
                }
            }
        });
//        userRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UserActivity.this,MyRequestActivity.class);
//                startActivity(intent);
//            }
//        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.getInstance(UserActivity.this,"Are you sure?","Do you want to log out?").setPositiveButton("Yes",(dialog, which) -> {
                    MainActivity.loginResponse=null;
                    finish();
                }).setNegativeButton("No",null).show();
            }
        });
        FirstLogin();
    }

    private void FirstLogin() {
        if (MainActivity.loginResponse.getFirstLogin() == true) {
            openDialog();

        }
    }

    public void openDialog() {
        Custom_Dialog custom_dialog = new Custom_Dialog();
        custom_dialog.show(getSupportFragmentManager(), "First login");
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.changePass:
                changePass();
                return true;
            case R.id.myAssignment:
                Intent intent = new Intent(UserActivity.this,MyAssignmentActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent2 = new Intent(UserActivity.this,UserInfoActivity.class);
                startActivity(intent2);
                return true;
            default:
                return false;
        }
    }
    public void showMenu(View v) {

        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.user);
        popup.show();
    }
    public void changePass()
    {
        Custom_Dialog_Changpass custom_dialog = new Custom_Dialog_Changpass();
        custom_dialog.show(getSupportFragmentManager(), "First login");
    }
}