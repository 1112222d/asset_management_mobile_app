package com.example.assets.UserActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.AdminActivity.EditUserActivity;
import com.example.assets.AlterDialog.Custom_Dialog;
import com.example.assets.AlterDialog.Custom_Dialog_Changpass;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Gender;
import com.example.assets.Model.User;
import com.example.assets.Model.UserRequest;
import com.example.assets.MyErrorMessage;
import com.example.assets.R;
import com.example.assets.Utils.VNCharacterUtils;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity  {

    EditText firstname, lastname,ed_email;
    TextView tv_name, gender, dob, type, joindate, ed_joindate;
    RelativeLayout progress;
    ImageView back, editIcon;
    User user;
    Boolean status = false;
    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pattern pattern = Pattern.compile(regex);
        setContentView(R.layout.activity_user_info);
        firstname = findViewById(R.id.ed_firstnameEdit);
        lastname = findViewById(R.id.ed_lastnameEdit);
        tv_name = findViewById(R.id.tv_nameEdit);
        gender = findViewById(R.id.ed_genderEdit);
        dob = findViewById(R.id.ed_dobEdit);
        type = findViewById(R.id.ed_typeEdit);
        joindate = findViewById(R.id.tv_accountEdit);
        ed_joindate = findViewById(R.id.ed_joinedDateEdit);
        progress = findViewById(R.id.prgrsbarUserEdit);
        back = findViewById(R.id.backCreateUserEdit);
        editIcon = findViewById(R.id.editAssetIcon);
        ed_email=findViewById(R.id.ed_email);
        ed_email.setEnabled(false);
        back.setOnClickListener(v -> finish());
        MainActivity.service.getUserStaff().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.code()==200)
                {
                    setData(response.body());
                    user=response.body();
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        firstname.setEnabled(false);
        lastname.setEnabled(false);
        ed_email=findViewById(R.id.ed_email);

    }


    private void setData(User data) {
        firstname.setText(data.getFirstName());
        lastname.setText(data.getLastName());
        tv_name.setText(data.getFullName());
        gender.setText(data.getGender());
        dob.setText(data.getDateOfBirth());
        type.setText(data.getType().equals("ROLE_ADMIN") ? "Admin" : "Staff");
        joindate.setText(data.getJoinedDate());
        ed_joindate.setText(data.getJoinedDate());
        if(!data.getEmail().equals("")) ed_email.setText(data.getEmail());
    }






}