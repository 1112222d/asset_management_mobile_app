package com.example.assets.AdminActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.assets.Adapter.CustomListViewSearchAssetAdapter;
import com.example.assets.Adapter.CustomListViewSearchUserAdapter;
import com.example.assets.Adapter.CustomListViewShowAssetSelectedAdapter;
import com.example.assets.Adapter.SearchModel;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.User;
import com.example.assets.MyErrorMessage;
import com.example.assets.R;
import com.example.assets.UserActivity.CreateNewAssignActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.SimpleSearchFilter;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewAssignmentActivity extends AppCompatActivity {
    RelativeLayout userSearch, prgrsbarAssign;
    List<Asset> assets;
    List<Asset> listSearchAsset;
    List<User> users;
    List<User> listSearchUser;
    User userSelect;
    ImageView backBackAsset, assetSearch;
    TextView tv_userSelect, tv_assignedDate, ed_assignedDate, tv_nameAssign, ed_returnDate;
    Button button_createAssign;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    CustomListViewSearchUserAdapter customListViewSearchUserAdapter;
    CustomListViewSearchAssetAdapter customListViewSearchAssetAdapter;
    CustomListViewShowAssetSelectedAdapter customListViewShowAssetSelectedAdapter;
    Dialog dialog;
    final Calendar calendar = Calendar.getInstance();
    long assignedDate, returnDate;
    Assignment assignment;
    ListView lv_assetSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_assignment);
        userSearch = findViewById(R.id.userSearch);
        assetSearch = findViewById(R.id.add_asset);
        lv_assetSelect = findViewById(R.id.lv_assetSelect);
        assets = new ArrayList<>();
        users = new ArrayList<>();
        listSearchUser = new ArrayList<>();
        listSearchAsset = new ArrayList<>();
        tv_userSelect = findViewById(R.id.TV_UserSelect);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_assignedDate = findViewById(R.id.ed_assignedDate);
        ed_returnDate = findViewById(R.id.ed_returnDate);
        tv_nameAssign = findViewById(R.id.tv_nameAssign);
        button_createAssign = findViewById(R.id.button_createAssign);
        prgrsbarAssign = findViewById(R.id.prgrsbarAssign);
        tv_assignedDate.setText("Assigned Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_assignedDate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        assignedDate = new Date().getTime();
        returnDate = new Date().getTime();
        assignment = new Assignment();
        customListViewShowAssetSelectedAdapter = new CustomListViewShowAssetSelectedAdapter(this, assignment.getAssignmentDetails());
        backBackAsset = findViewById(R.id.backBackAsset);
        backBackAsset.setOnClickListener(v -> finish());
        lv_assetSelect.setAdapter(customListViewShowAssetSelectedAdapter);
        load();
        userSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(CreateNewAssignmentActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);
                customListViewSearchUserAdapter = new CustomListViewSearchUserAdapter(CreateNewAssignmentActivity.this, listSearchUser);
                listView.setAdapter(customListViewSearchUserAdapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchUser(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        userSelect = (User) customListViewSearchUserAdapter.getItem(position);
                        Toast.makeText(CreateNewAssignmentActivity.this, "" + userSelect.getStaffCode(), Toast.LENGTH_SHORT).show();
                        tv_userSelect.setText(userSelect.getFullName());
                        tv_nameAssign.setText(userSelect.getFullName());
                        dialog.dismiss();
                    }
                });
            }
        });
        assetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(CreateNewAssignmentActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_asset_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);
                customListViewSearchAssetAdapter = new CustomListViewSearchAssetAdapter(CreateNewAssignmentActivity.this, listSearchAsset);
                listView.setAdapter(customListViewSearchAssetAdapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchAsset(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Asset asset = (Asset) customListViewSearchAssetAdapter.getItem(position);
                        assignment.getAssignmentDetails().add(new Assignment.AssignmentDetail(asset.getAssetCode(), asset.getAssetName(), asset.getCategoryName(), asset.getSpecification(), asset.getState(), null));
                        customListViewShowAssetSelectedAdapter.notifyDataSetChanged();
                        Toast.makeText(CreateNewAssignmentActivity.this, "" + asset.getAssetCode(), Toast.LENGTH_SHORT).show();
                        load();
                        dialog.dismiss();
                    }
                });
            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                prgrsbarAssign.setVisibility(View.VISIBLE);
                Boolean check = true;
                if (userSelect == null) {
                    MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                            "Please choose user!").show();
                    check = false;
                }
                if (assignment.getAssignmentDetails().size() == 0) {
                    MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                            "Please choose asset!").show();
                    check = false;
                }
                if (ed_assignedDate.getText().toString().equals("dd/MM/yyyy")) {
                    MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                            "Please choose date!").show();
                    check = false;
                } else {
                    String message = checkDate(ed_assignedDate.getText().toString());
                    if (!message.equals("")) {
                        MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                                message).show();
                        check = false;
                    }
                }
                if (ed_returnDate.getText().toString().equals("dd/MM/yyyy")) {
                    MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                            "Please choose date!").show();
                    check = false;
                } else {
                    String message = checkDate(ed_returnDate.getText().toString());
                    if (!message.equals("")) {
                        MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                                message).show();
                        check = false;
                    }
                }
                if (check) {
                    assignment.setAssignedTo(userSelect.getUsername());
                    assignment.setNote("");
                    assignment.setAssignedDate(ed_assignedDate.getText().toString());
                    assignment.setIntendedReturnDate(ed_returnDate.getText().toString());
                    assignment.getAssignmentDetails().stream().forEach(x->x.setState(null));
                    MainActivity.service.createAssignment(assignment).enqueue(new Callback<Assignment>() {
                        @Override
                        public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                            prgrsbarAssign.setVisibility(View.INVISIBLE);
                            if (response.code() == 200) {
                                MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Success",
                                        "Create assignment success").show();
                                AssignmentManagementActivity.assignmentNew = response.body();
                                load();
                                reload();

                            } else {
                                Gson gson = new Gson();
                                MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                                MessageDialog.getInstance(CreateNewAssignmentActivity.this, message.getError(),
                                        message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                                }).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Assignment> call, Throwable t) {
                            prgrsbarAssign.setVisibility(View.INVISIBLE);
                            MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
                                    "Something went wrong").show();
                        }
                    });
                } else {
                    prgrsbarAssign.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void reload() {
        userSelect = null;
        tv_assignedDate.setText("Assigned Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_assignedDate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        tv_userSelect.setText("User");

        tv_nameAssign.setText("New Assignment");
        assignedDate = new Date().getTime();
        returnDate = new Date().getTime();
        calendar.clear();
    }

    private void load() {
        MainActivity.service.getAllUser().enqueue(new Callback<List<User>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    users.clear();
                    listSearchUser.clear();
//                    users.addAll(response.body().stream().filter(x->(!x.getStaffCode().equals(MainActivity.loginResponse.getStaffCode()))&&x.getState().equals("Enable")).collect(Collectors.toList()));
//                    listSearchUser.addAll(response.body().stream().filter(x->(!x.getStaffCode().equals(MainActivity.loginResponse.getStaffCode()))&&x.getState().equals("Enable")).collect(Collectors.toList()));
                    users.addAll(response.body().stream().filter(x -> (x.getState().equals("Enable") && !x.getStaffCode().equals(MainActivity.loginResponse.getStaffCode()))).collect(Collectors.toList()));
                    listSearchUser.addAll(users);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
        MainActivity.service.getAllAsset().enqueue(new Callback<List<Asset>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Asset>> call, Response<List<Asset>> response) {
                if (response.code() == 200) {
                    if(assets.size()==0)
                        assets.addAll(response.body().stream().filter(x->x.getState().equals("AVAILABLE")).collect(Collectors.toList()));
                    listSearchAsset.clear();
                    response.body().stream().forEach(x -> {
                        if (x.getState().equals("AVAILABLE")) {
                            if (!assignment.getAssignmentDetails().stream().anyMatch(y -> x.getAssetCode().equals(y.getAssetCode()))) {
                                listSearchAsset.add(x);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {

            }
        });
    }

    public void selectAssignedDate(View v) {
        builder.setTitleText("Select assigned date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        if (assignedDate != 0) {
            calendar.setTime(new Date(assignedDate));
            builder.setSelection(calendar.getTimeInMillis());
        }
        materialDatePicker = builder.build();
        calendar.clear();
        materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            assignedDate = (long) selection;
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(assignedDate)).toString();
            ed_assignedDate.setText(dateString);
            tv_assignedDate.setText("Assigned Date: " + dateString);

        });

    }

    public void selectReturnedDate(View v) {
        builder.setTitleText("Select returned date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        if (returnDate != 0) {
            calendar.setTime(new Date(returnDate));
            builder.setSelection(calendar.getTimeInMillis());
        }
        materialDatePicker = builder.build();
        calendar.clear();
        materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            returnDate = (long) selection;
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(returnDate)).toString();
            ed_returnDate.setText(dateString);

        });

    }

    private String checkDate(String date) {
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        Date assignedDate = new Date();
        try {
            assignedDate = format.parse(date);
        } catch (Exception ex) {
            return "Wrong date! (dd/MM/yyyy)";
        }

        long diff = TimeUnit.DAYS.convert(assignedDate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
        if (diff < 0) {
            return "Assigned Date need to be equal or bigger than current date";
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchUser(String input) {
        listSearchUser.clear();
        String key = input.replaceAll("\\s{2,}", " ").trim();
        if (!key.equals("")) {
            listSearchUser.addAll(users.stream().filter(x -> {
                return x.getFullName().toLowerCase().contains(key.toLowerCase()) || x.getStaffCode().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            customListViewSearchUserAdapter.notifyDataSetChanged();
        } else {
            listSearchUser.addAll(users);
            customListViewSearchUserAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void searchAsset(String input) {
        listSearchAsset.clear();
        String key = input.replaceAll("\\s{2,}", " ").trim();
        if (!key.equals("")) {
            assets.stream().forEach(x -> {
                if (x.getState().equals("AVAILABLE")) {
                    if ((!assignment.getAssignmentDetails().stream().anyMatch(y -> x.getAssetCode().equals(y.getAssetCode())))
                            && (x.getAssetCode().toLowerCase().contains(key.toLowerCase()) || x.getAssetName().toLowerCase().contains(key.toLowerCase()))
                    ) {
                        listSearchAsset.add(x);
                    }
                }
            });
            customListViewSearchAssetAdapter.notifyDataSetChanged();
        } else {
            listSearchAsset.addAll(assets);
            customListViewSearchAssetAdapter.notifyDataSetChanged();
        }
    }
}