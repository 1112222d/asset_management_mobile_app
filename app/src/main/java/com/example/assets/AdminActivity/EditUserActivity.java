package com.example.assets.AdminActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.Adapter.CustomListViewDepartmentAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Department;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    EditText firstname, lastname,ed_email;
    TextView tv_name, gender, dob, type, joindate, ed_joindate,id_user_edit;
    Button button,delete;
    long dateJoin, dateOB;
    TextView edDepartment;
    Dialog dialog;
    Department deptSelect;
    public CustomListViewDepartmentAdapter customListViewDepartmentAdapter;
    List<Department> departments;
    RelativeLayout progress;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    ImageView back, editIcon;
    User user;
    Boolean status = false;
    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pattern pattern = Pattern.compile(regex);
        setContentView(R.layout.activity_edit_user);
        firstname = findViewById(R.id.ed_firstnameEdit);
        lastname = findViewById(R.id.ed_lastnameEdit);
        tv_name = findViewById(R.id.tv_nameEdit);
        gender = findViewById(R.id.ed_genderEdit);
        dob = findViewById(R.id.ed_dobEdit);
        type = findViewById(R.id.ed_typeEdit);
        joindate = findViewById(R.id.tv_accountEdit);
        ed_joindate = findViewById(R.id.ed_joinedDateEdit);
        button = findViewById(R.id.button_signinEdit);
        progress = findViewById(R.id.prgrsbarUserEdit);
        back = findViewById(R.id.backCreateUserEdit);
        editIcon = findViewById(R.id.editAssetIcon);
        id_user_edit=findViewById(R.id.id_user_edit);
        delete=findViewById(R.id.button_UserDelete);
        ed_email=findViewById(R.id.ed_email);
        edDepartment =findViewById(R.id.ed_depart);
        departments=new ArrayList<>();
        customListViewDepartmentAdapter= new CustomListViewDepartmentAdapter(EditUserActivity.this,departments);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        firstname.setEnabled(false);
        lastname.setEnabled(false);
        checkDelete(user.getStaffCode());

        setStatus(status);
        setData(user);
        delete.setOnClickListener(v -> {
            MainActivity.service.canDisableUser(user.getStaffCode()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.code()==200)
                    {
                        disableUser(user);
                    }else
                    {
                        if(response.code()==202)
                        {
                            deleteUser(user);
                        }else
                        {
                            if(response.code()==409)
                            {
                                MessageDialog.getInstance(EditUserActivity.this, "Error",
                                        "There are valid assignments belonging to user.\n Please close all assignments before disabling user").setPositiveButton("OK", (dialog, which) -> {
                                }).show();
                            }else
                                MessageDialog.getInstance(EditUserActivity.this,"Error","Something went wrong").show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {

                }
            });
        });
        back.setOnClickListener(v -> finish());
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = !status;
                setStatus(status);
                Toast.makeText(EditUserActivity.this, "Edit mode"+ (status?"ON":"OFF"), Toast.LENGTH_SHORT).show();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGender(v, status);

            }
        });
        ed_joindate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectJoined(v, status);
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v, status);
            }
        });
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showType(v, status);
            }
        });
        button.setOnClickListener(v -> {
            checkInput();
            Gender genderTemp = Gender.Male;
            progress.setVisibility(View.VISIBLE);
            firstname.setText(VNCharacterUtils.removeAccent(firstname.getText().toString()));
            lastname.setText(VNCharacterUtils.removeAccent(lastname.getText().toString()));

            Boolean check = true;
            if (firstname.getText().toString().equals("")) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Firstname must not blank").show();
                check = false;
            }
            if (lastname.getText().toString().equals("")) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Lastname must not blank").show();
                check = false;
            }
            if (dob.getText().toString().equals("dd/MM/yyyy")) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Date of birth must not blank").show();
                check = false;
            }
            Date join = null, ob = null;
            try {
                join = new SimpleDateFormat("dd/MM/yyyy").parse(ed_joindate.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                ob = new SimpleDateFormat("dd/MM/yyyy").parse(dob.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (join.before(ob)) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Joined date is not later than Date of Birth. Please select a different date").show();
                check = false;
            }
            if (!checkAge(ob, join)) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "User is under 18. Please select a different date").show();
                check = false;
            }
            if(!pattern.matcher(ed_email.getText().toString()).matches())
            {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Email not available").show();
                check=false;
            }
            int day = getDayNumberOld(join);
            if (day == 7 || day == 1) {
                MessageDialog.getInstance(EditUserActivity.this, "Error",
                        "Joined date is Saturday or Sunday. Please select a different date").show();
                check = false;
            }

            if (gender.getText().toString().equals("Female")) genderTemp = Gender.Female;
            if (check) {

                UserRequest request = new UserRequest();
                request.setFirstName(firstname.getText().toString());
                request.setLastName(lastname.getText().toString());
                request.setDateOfBirth(dob.getText().toString());
                request.setJoinedDate(ed_joindate.getText().toString());
                request.setGender(genderTemp);
                request.setType(type.getText().toString());
                request.setEmail(ed_email.getText().toString());
                request.setDeptCode(deptSelect.getDeptCode());
                MainActivity.service.updateUser(request, user.getStaffCode()).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progress.setVisibility(View.INVISIBLE);
                        if (response.code() == 200) {
                            MessageDialog.getInstance(EditUserActivity.this, "Success",
                                    "Update user success").setPositiveButton("OK", (dialog, which) -> {
                                finish();
                            }).show();
                        } else {
                            Gson gson = new Gson();
                            MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                            MessageDialog.getInstance(EditUserActivity.this, "Error",
                                    message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                            }).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progress.setVisibility(View.VISIBLE);
                        MessageDialog.getInstance(EditUserActivity.this, "Error",
                                "Something went wrong").show();
                    }
                });
            } else {
                progress.setVisibility(View.INVISIBLE);
            }
        });
        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_name.setText(firstname.getText().toString() + " " + lastname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_name.setText(firstname.getText().toString() + " " + lastname.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkInput();
            }
        });
        firstname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkInput();
            }
        });
        lastname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                checkInput();
            }
        });
        edDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(EditUserActivity.this);
                dialog.setContentView(R.layout.dialog_department_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView title =dialog.findViewById(R.id.title);
                ListView listView = dialog.findViewById(R.id.listView);
                Button button = dialog.findViewById(R.id.create);
                title.setText("Choose Department");
                listView.setAdapter(customListViewDepartmentAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        deptSelect = (Department) customListViewDepartmentAdapter.getItem(position);
                        Toast.makeText(EditUserActivity.this, "" + deptSelect.getDeptCode(), Toast.LENGTH_SHORT).show();
                        edDepartment.setText(deptSelect.getName());
                        dialog.dismiss();
                    }
                });
                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        PopupMenu popup = new PopupMenu(view.getContext(), view);
                        popup.inflate(R.menu.longclick);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Department department =(Department)customListViewDepartmentAdapter.getItem(i);
                                switch (item.getItemId()) {
                                    case R.id.edit: {
//                                editUser((Department) customDepartmentSpinnerAdapter.getItem(i));
                                        Toast.makeText(EditUserActivity.this, "Edit" + department.getDeptCode(), Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    case R.id.delete: {
//                                deleteUser((Department) customDepartmentSpinnerAdapter.getItem(i));
                                        Toast.makeText(EditUserActivity.this, "Delete" +department.getDeptCode(), Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.show();
                        return true;
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(EditUserActivity.this, "Create new", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public void showGender(View v, Boolean status) {
        if (status) {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.gender);
            popup.show();
        }
    }

    public void showType(View v, Boolean status) {
        if (status) {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.type);
            popup.show();
        }
    }

    public void checkInput()
    {
        if(!firstname.getText().toString().equals(""))
        {
            firstname.setText(VNCharacterUtils.removeAccent(firstname.getText().toString()));
            firstname.setText(VNCharacterUtils.toTitleCase(firstname.getText().toString()));
        }
        if(!lastname.getText().toString().equals(""))
        {
            lastname.setText(VNCharacterUtils.removeAccent(lastname.getText().toString()));
            lastname.setText(VNCharacterUtils.toTitleCase(lastname.getText().toString()));
        }
    }
    public void selectDate(View v, Boolean status) {
        if (status) {
            builder.setTitleText("Select your birthday");
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            if(dateOB!=0)
            {
                calendar.setTime(new Date(dateOB));
                builder.setSelection(calendar.getTimeInMillis());
            }
            materialDatePicker = builder.build();
            calendar.clear();
            materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                dateOB = (long) selection;
                String dateString = DateFormat.format("dd/MM/yyyy", new Date(dateOB)).toString();
                dob.setText(dateString);


            });
        }

    }

    public void selectJoined(View v, Boolean status) {
        if (status) {
            builder.setTitleText("Select your joined date");
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            if(dateJoin!=0)
            {
                calendar.setTime(new Date(dateJoin));
                builder.setSelection(calendar.getTimeInMillis());
            }
            materialDatePicker = builder.build();
            calendar.clear();
            materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                dateJoin = (long) selection;
                String dateString = DateFormat.format("dd/MM/yyyy", new Date(dateJoin)).toString();
                ed_joindate.setText(dateString);
                joindate.setText("Joined date: " + dateString);

            });
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.male:
                gender.setText("Male");
                return true;
            case R.id.female:
                gender.setText("Female");
                return true;
            case R.id.admin:
                type.setText("Admin");
                return true;
            case R.id.staff:
                type.setText("Staff");
                return true;
            default:
                return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkAge(Date dOB, Date joinDate) {
        LocalDate date1 = dOB.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate date2 = joinDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate now = LocalDate.now();
        Period period = Period.between(date1, date2);
        int temp = period.getYears();
        return period.getYears() >= 18 ? true : false;
    }

    private int getDayNumberOld(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private void setData(User data) {
        id_user_edit.setText("ID: "+data.getStaffCode());
        firstname.setText(data.getFirstName());
        lastname.setText(data.getLastName());
        tv_name.setText(data.getFullName());
        gender.setText(data.getGender());
        dob.setText(data.getDateOfBirth());
        type.setText(data.getType().equals("ROLE_ADMIN") ? "Admin" : "Staff");
        joindate.setText(data.getJoinedDate());
        ed_joindate.setText(data.getJoinedDate());
        edDepartment.setText(data.getDeptName());
        deptSelect= new Department(data.getDeptCode(),data.getDeptName());
        List<String> temp= Arrays.asList(data.getDateOfBirth().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        dateOB=calendar.getTimeInMillis();
        temp=Arrays.asList(data.getJoinedDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        dateJoin=calendar.getTimeInMillis();
        if(data.getEmail()!=null) ed_email.setText(data.getEmail());
        else ed_email.setText("");
    }

    private void setStatus(Boolean status) {
        ed_email.setEnabled(status);
        button.setEnabled(status);
        edDepartment.setEnabled(status);
    }

    private void disableUser(User user) {
        MessageDialog.getInstance(EditUserActivity.this, "Are you sure",
                "Do you want to disable this user ? (" + user.getUsername() + ")").setPositiveButton("Disable", (dialog, which) -> {
            progress.setVisibility(View.VISIBLE);
            MainActivity.service.disableUser(user.getStaffCode()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.code() == 200) {
                        Toast.makeText(EditUserActivity.this, "Disable success", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.INVISIBLE);
                        finish();
                    } else {
                        MessageDialog.getInstance(EditUserActivity.this, "Error",
                                "There are valid assignments belonging to user.\n Please close all assignments before disabling user").setPositiveButton("OK", (dialog, which) -> {
                        }).show();

                        progress.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(EditUserActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }).show();
    }
    private void deleteUser(User user) {
        MessageDialog.getInstance(EditUserActivity.this, "Are you sure",
                "Do you want to delete this user ? (" + user.getUsername() + ")").setPositiveButton("Delete", (dialog, which) -> {
            progress.setVisibility(View.VISIBLE);
            MainActivity.service.disableUser(user.getStaffCode()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.code() == 200) {
                        Toast.makeText(EditUserActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
                        progress.setVisibility(View.INVISIBLE);
                        finish();
                    } else {
                        MessageDialog.getInstance(EditUserActivity.this, "Error",
                                "There are valid assignments belonging to user.\n Please close all assignments before disabling user").setPositiveButton("OK", (dialog, which) -> {
                        }).show();

                        progress.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(EditUserActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    progress.setVisibility(View.INVISIBLE);
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }).show();
    }
    private void checkDelete(String staffcode)
    {
         MainActivity.service.canDisableUser(staffcode).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                int code =response.code();
                switch (code)
                {
                    case 409:
                    case 200:
                        delete.setEnabled(true);
                        delete.setText("Disable");
                        break;
                    case 202:
                        delete.setEnabled(true);
                        delete.setText("Delete");

                        break;
                    default:
                        delete.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

            }
        });
    }
    private void loadDepart(){
        MainActivity.service.getAllDepart().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if(response.code()==200)
                {
                    departments.clear();
                    departments.addAll(response.body());
                    customListViewDepartmentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDepart();
    }
}