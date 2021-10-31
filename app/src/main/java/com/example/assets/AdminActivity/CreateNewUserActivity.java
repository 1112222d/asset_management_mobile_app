package com.example.assets.AdminActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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
import com.example.assets.Model.Department;
import com.example.assets.Model.Gender;
import com.example.assets.Model.User;
import com.example.assets.Model.UserRequest;
import com.example.assets.R;
import com.example.assets.Utils.VNCharacterUtils;
import com.google.android.material.datepicker.MaterialDatePicker;

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
import java.util.regex.Pattern;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewUserActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    EditText firstname, lastname,ed_email;
    TextView tv_name, gender, dob, type, joindate, ed_joindate;
    Button button;
    TextView edDepartment;
    Dialog dialog;
    Department deptSelect;
    public CustomListViewDepartmentAdapter customListViewDepartmentAdapter;
    List<Department> departments;
    long dateJoin=0, dateOB=0;
    RelativeLayout progress;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;

    final Calendar calendar = Calendar.getInstance();
    ImageView back;
    private static final String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Pattern pattern = Pattern.compile(regex);
        setContentView(R.layout.activity_create_new_user);
        firstname = findViewById(R.id.ed_firstname);
        lastname = findViewById(R.id.ed_lastname);
        tv_name = findViewById(R.id.tv_name);
        gender = findViewById(R.id.ed_gender);
        dob = findViewById(R.id.ed_dob);
        type = findViewById(R.id.ed_type);
        joindate = findViewById(R.id.tv_account);
        ed_joindate = findViewById(R.id.ed_joinedDate);
        button = findViewById(R.id.button_signin);
        progress = findViewById(R.id.prgrsbarUser);
        edDepartment =findViewById(R.id.ed_depart);

        departments=new ArrayList<>();
        customListViewDepartmentAdapter= new CustomListViewDepartmentAdapter(CreateNewUserActivity.this,departments);
        deptSelect = new Department();
        joindate.setText("Installed Date: "+DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_joindate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        dateJoin=new Date().getTime();
        dob.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        dateOB=new Date().getTime();
        back = findViewById(R.id.backCreateUser);
        back.setOnClickListener(v -> finish());
        ed_email=findViewById(R.id.ed_email);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                    for (int i = start; i < end; i++) {
                        if (Character.isWhitespace(source.charAt(i)) || Character.isDigit(source.charAt(i))) {
                            return source.subSequence(start, end-1);
                        }
                    }
                    if(source.length()>20)
                    {
                        return source.subSequence(0,20);
                    }else
            return null;
            }
        };
        InputFilter filter2 = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {


                int number= Arrays.asList(lastname.getText().toString().split(" ")).size();
                System.out.println(number);
                for (int i = start; i < end; i++) {
                    if ((Character.isWhitespace(source.charAt(i))|| Character.isDigit(source.charAt(i)))&&number>6) {
                        return source.subSequence(start, end-1);
                    }
                }
                if(source.length()>100)
                {
                    return source.subSequence(0,100);
                }else
                    return null;
            }

        };
        firstname.setFilters(new InputFilter[]{filter});
        lastname.setFilters(new InputFilter[]{filter2});

        firstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                tv_name.setText(firstname.getText().toString() + " " + lastname.getText().toString());
                System.out.println(s.toString());
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
                dialog = new Dialog(CreateNewUserActivity.this);
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
                        Toast.makeText(CreateNewUserActivity.this, "" + deptSelect.getDeptCode(), Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(CreateNewUserActivity.this, "Edit" + department.getDeptCode(), Toast.LENGTH_SHORT).show();
                                        return true;
                                    }
                                    case R.id.delete: {
//                                deleteUser((Department) customDepartmentSpinnerAdapter.getItem(i));
                                        Toast.makeText(CreateNewUserActivity.this, "Delete" +department.getDeptCode(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CreateNewUserActivity.this, "Create new", Toast.LENGTH_SHORT).show();
                    }
                });
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
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "Firstname must not blank").show();
                check = false;
            }
            if (lastname.getText().toString().equals("")) {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "Lastname must not blank").show();
                check = false;
            }
            if (dob.getText().toString().equals("dd/MM/yyyy")) {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "Date of birth must not blank").show();
                check = false;
            }
            if (new Date(dateJoin).before(new Date(dateOB))) {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "Joined date is not later than Date of Birth. Please select a different date").show();
                check = false;
            }
            if (!checkAge(new Date(dateOB), new Date(dateJoin))) {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "User is under 18. Please select a different date").show();
                check = false;
            }
            if(!pattern.matcher(ed_email.getText().toString()).matches())
            {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                        "Email not available").show();
                check = false;
            }
            int day = getDayNumberOld(new Date(dateJoin));
            if (day == 7 || day == 1) {
                MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
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
                MainActivity.service.createUser(request).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progress.setVisibility(View.INVISIBLE);
                        if (response.code() == 201) {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat formatter2 = new SimpleDateFormat("ddMMyyyy");
                            Date date = null;
                            try {
                                date = formatter.parse(response.body().getDateOfBirth());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            MessageDialog.getInstance(CreateNewUserActivity.this, "Success",
                                    "Create user success\nYour Username: "+response.body().getUsername()+"\nPassword: "+response.body().getUsername()+"@"+formatter2.format(date)).setPositiveButton("OK", (dialog, which) -> {
                                UserManagementActivity.userNew=response.body();
                                finish();
                            }).show();
                        } else {
                            MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                                    "Email is exists").setPositiveButton("OK", (dialog, which) -> {
                            }).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progress.setVisibility(View.VISIBLE);
                        MessageDialog.getInstance(CreateNewUserActivity.this, "Error",
                                "Something went wrong").show();
                    }
                });
            } else {
                progress.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showGender(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.gender);
        popup.show();
    }

    public void showType(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.type);
        popup.show();
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
    public void selectDate(View v) {
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

    public void selectJoined(View v) {
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
    private void loadDepart(){
        MainActivity.service.getAllDepart().enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if(response.code()==200)
                {
                    departments.clear();
                    departments.addAll(response.body());
                    customListViewDepartmentAdapter.notifyDataSetChanged();
                    edDepartment.setText(departments.get(0).getName());
                    deptSelect= departments.get(0);
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