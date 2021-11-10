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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssignmentActivity extends AppCompatActivity {

    RelativeLayout userSearch,prgrsbarAssign;
    List<Asset> assets;
    List<Asset> listSearchAsset;
    List<User> users,listSearchUser;
    User userSelect;
    Asset assetSelect;
    CustomListViewSearchUserAdapter customListViewSearchUserAdapter;
    CustomListViewSearchAssetAdapter customListViewSearchAssetAdapter;
    CustomListViewShowAssetSelectedAdapter customListViewShowAssetSelectedAdapter;
    Dialog dialog;
    ListView lv_assetSelect;
    TextView tv_userSelect,ed_assignedDate,tv_nameAssign,tv_asset,ed_returnDate,tv_assignedDate;
    Button button_createAssign;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    long assignedDate,returnDate;
    Assignment assignment;
    ImageView edit,backBackAsset,assetSearch;
    Boolean status=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        userSearch = findViewById(R.id.userSearch);
        assetSearch = findViewById(R.id.add_asset);
        assets = new ArrayList<>();
        users = new ArrayList<>();
        assetSelect=new Asset();
        userSelect=new User();
        listSearchUser = new ArrayList<>();
        listSearchAsset = new ArrayList<>();
        tv_userSelect=findViewById(R.id.TV_UserSelect);
        ed_returnDate = findViewById(R.id.ed_returnDate);
        ed_assignedDate=findViewById(R.id.ed_assignedDate);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        lv_assetSelect = findViewById(R.id.lv_assetSelect);
        tv_nameAssign=findViewById(R.id.tv_nameAssign);
        tv_asset=findViewById(R.id.tv_asset);
        button_createAssign=findViewById(R.id.button_createAssign);
        prgrsbarAssign=findViewById(R.id.prgrsbarAssign);
        Intent intent = getIntent();
        edit=findViewById(R.id.edit);
        assignment = (Assignment) intent.getSerializableExtra("assignment");
        backBackAsset=findViewById(R.id.backBackAsset);
        backBackAsset.setOnClickListener(v -> finish());
        setData(assignment);
        setStatus(status);
        load();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignment.getState().equals("WAITING_FOR_ACCEPTANCE"))
                {
                    status=!status;
                    setStatus(status);
                    Toast.makeText(EditAssignmentActivity.this, "Edit mode"+ (status?"ON":"OFF"), Toast.LENGTH_SHORT).show();
                }else
                {
                    MessageDialog.getInstance(EditAssignmentActivity.this, "ERR0R",
                            "Assignment state must be Waiting for acceptance").show();
                }
            }
        });
        userSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(EditAssignmentActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);
                customListViewSearchUserAdapter = new CustomListViewSearchUserAdapter(EditAssignmentActivity.this, listSearchUser);
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
                        Toast.makeText(EditAssignmentActivity.this, "" + userSelect.getStaffCode(), Toast.LENGTH_SHORT).show();
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
                dialog = new Dialog(EditAssignmentActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_asset_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);
                customListViewSearchAssetAdapter = new CustomListViewSearchAssetAdapter(EditAssignmentActivity.this, listSearchAsset);
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
                        Toast.makeText(EditAssignmentActivity.this, "" + asset.getAssetCode(), Toast.LENGTH_SHORT).show();
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
                    MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                            "Please choose user!").show();
                    check = false;
                }
                if (assignment.getAssignmentDetails().size() == 0) {
                    MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                            "Please choose asset!").show();
                    check = false;
                }
                if (ed_assignedDate.getText().toString().equals("dd/MM/yyyy")) {
                    MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                            "Please choose date!").show();
                    check = false;
                } else {
                    String message = checkDate(ed_assignedDate.getText().toString());
                    if (!message.equals("")) {
                        MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                                message).show();
                        check = false;
                    }
                }
                if (ed_returnDate.getText().toString().equals("dd/MM/yyyy")) {
                    MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                            "Please choose date!").show();
                    check = false;
                } else {
                    String message = checkDate(ed_returnDate.getText().toString());
                    if (!message.equals("")) {
                        MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
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
                                MessageDialog.getInstance(EditAssignmentActivity.this, "Success",
                                        "Create assignment success").show();
                                AssignmentManagementActivity.assignmentNew = response.body();
                                load();
                                reload();

                            } else {
                                Gson gson = new Gson();
                                MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                                MessageDialog.getInstance(EditAssignmentActivity.this, message.getError(),
                                        message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                                }).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Assignment> call, Throwable t) {
                            prgrsbarAssign.setVisibility(View.INVISIBLE);
                            MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
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
                    User u=response.body().stream().filter(x->x.getUsername().equals(assignment.getAssignedTo())).findFirst().orElse(null);
                    if(u!=null)
                    {
                        tv_userSelect.setText(u.getFullName());
                        tv_nameAssign.setText(u.getFullName());
                    }
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
    private void setStatus(Boolean status)
    {
        button_createAssign.setEnabled(status);
    }
    private void setData(Assignment data)
    {

        ed_assignedDate.setText(data.getAssignedDate());
        ed_returnDate.setText(data.getIntendedReturnDate());
        List<String> temp=Arrays.asList(data.getAssignedDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        assignedDate=calendar.getTimeInMillis();
        temp=Arrays.asList(data.getIntendedReturnDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        returnDate=calendar.getTimeInMillis();
        customListViewShowAssetSelectedAdapter = new CustomListViewShowAssetSelectedAdapter(this, assignment.getAssignmentDetails());
        lv_assetSelect.setAdapter(customListViewShowAssetSelectedAdapter);
//        switch (data.getState()) {
//            case "ACCEPTED":
//                state.setText("Accepted");
//                state.setBackground(getResources().getDrawable(R.drawable.bg_green_status));
//                break;
//            case "WAITING_FOR_ACCEPTANCE":
//                state.setText("Waiting for acceptance");
//                state.setBackground(getResources().getDrawable(R.drawable.bg_yellow_status));
//                break;
//            case "WAITING_FOR_RETURNING":
//                state.setText("Waiting for returning");
//                state.setBackground(getResources().getDrawable(R.drawable.bg_orange_status));
//                break;
//            case "COMPLETED":
//                state.setText("Completed");
//                state.setBackground(getResources().getDrawable(R.drawable.bg_blue_status));
//                break;
//            case "CANCELED_ASSIGN":
//                state.setText("Declined");
//                state.setBackground(getResources().getDrawable(R.drawable.bg_red_status));
//                break;
//            default:
//                break;
//        }
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
}