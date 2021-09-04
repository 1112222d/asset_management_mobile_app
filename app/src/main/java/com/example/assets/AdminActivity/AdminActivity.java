package com.example.assets.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.assets.AlterDialog.Custom_Dialog;
import com.example.assets.AlterDialog.Custom_Dialog_Changpass;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.R;
import com.example.assets.UserActivity.MyAssignmentActivity;
import com.example.assets.UserActivity.UserActivity;
import com.example.assets.UserActivity.UserInfoActivity;
import com.makeramen.roundedimageview.RoundedImageView;

public class AdminActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    LinearLayout assetManagement;
    LinearLayout userManagement;
    LinearLayout borrowManagement;
    LinearLayout statisticManagement,logout,returnRequests;
    RoundedImageView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        assetManagement = findViewById(R.id.assetManagement);
        userManagement = findViewById(R.id.userManagement);
        borrowManagement = findViewById(R.id.borrowManagement);
        statisticManagement = findViewById(R.id.statistics);
        returnRequests=findViewById(R.id.returnRequests);
        user=findViewById(R.id.imageProfileComment);
        logout=findViewById(R.id.logout);
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
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.getInstance(AdminActivity.this,"Are you sure?","Do you want to log out?").setPositiveButton("Yes",(dialog, which) -> {
                    MainActivity.loginResponse=null;
                    finish();
                }).setNegativeButton("No",null).show();
            }
        });
        assetManagement.setOnClickListener(v -> {
            if (MainActivity.loginResponse.getFirstLogin()) {
                openDialog();
            } else {

                Intent intent = new Intent(AdminActivity.this, AssetManagementActivity.class);
                startActivity(intent);
            }
        });
        userManagement.setOnClickListener(v -> {
            if (MainActivity.loginResponse.getFirstLogin()) {
                openDialog();
            } else {
                Intent intent = new Intent(AdminActivity.this, UserManagementActivity.class);
                startActivity(intent);
            }
        });
        borrowManagement.setOnClickListener(v -> {
            if (MainActivity.loginResponse.getFirstLogin()) {
                openDialog();
            } else {
                Intent intent = new Intent(AdminActivity.this, AssignmentManagementActivity.class);
                startActivity(intent);
            }
        });
        statisticManagement.setOnClickListener(v -> {
            if (MainActivity.loginResponse.getFirstLogin()) {
                openDialog();
            } else {
                Intent intent = new Intent(AdminActivity.this,ReportActivity.class);
                startActivity(intent);
            }
        });
        returnRequests.setOnClickListener(v->{
            if (MainActivity.loginResponse.getFirstLogin()) {
                openDialog();
            } else {
                Intent intent = new Intent(AdminActivity.this, RequestActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(AdminActivity.this, MyAssignmentActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile:
                Intent intent2 = new Intent(AdminActivity.this,UserInfoActivity.class);
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