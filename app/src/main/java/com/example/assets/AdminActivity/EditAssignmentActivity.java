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
import com.example.assets.Adapter.SearchModel;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssignmentActivity extends AppCompatActivity {

    RelativeLayout userSearch, assetSearch,prgrsbarAssign;
    List<Asset> assets;
    List<Asset> listSearchAsset;
    List<User> users;
    List<User> listSearchUser;
    User userSelect;
    Asset assetSelect;
    EditText ed_note;
    CustomListViewSearchUserAdapter customListViewSearchUserAdapter;
    CustomListViewSearchAssetAdapter customListViewSearchAssetAdapter;
    Dialog dialog;

    TextView tv_userSelect,tv_assetSelect,tv_assignedDate,ed_assignedDate,tv_nameAssign,tv_asset,idAssignmentEdit,state;
    Button button_createAssign;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    long assignedDate;
    Assignment assignmentSelect;
    ImageView edit,backBackAsset;
    Boolean status=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);
        idAssignmentEdit=findViewById(R.id.idAssignmentEdit);
        userSearch = findViewById(R.id.userSearch);
        assetSearch = findViewById(R.id.assetSearch);
        state=findViewById(R.id.stateAssign);
        assets = new ArrayList<>();
        users = new ArrayList<>();
        assetSelect=new Asset();
        userSelect=new User();
        listSearchUser = new ArrayList<>();
        listSearchAsset = new ArrayList<>();
        tv_assetSelect=findViewById(R.id.TV_AssetSelect);
        tv_userSelect=findViewById(R.id.TV_UserSelect);
        tv_assignedDate=findViewById(R.id.tv_assignedDate);
        ed_assignedDate=findViewById(R.id.ed_assignedDate);
        ed_note=findViewById(R.id.ed_note);
        tv_nameAssign=findViewById(R.id.tv_nameAssign);
        tv_asset=findViewById(R.id.tv_asset);
        button_createAssign=findViewById(R.id.button_createAssign);
        prgrsbarAssign=findViewById(R.id.prgrsbarAssign);
        Intent intent = getIntent();
        edit=findViewById(R.id.edit);
        assignmentSelect = (Assignment) intent.getSerializableExtra("assignment");
        backBackAsset=findViewById(R.id.backBackAsset);
        backBackAsset.setOnClickListener(v -> finish());
        setData(assignmentSelect);
        setStatus(status);
        load();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(assignmentSelect.getState().equals("WAITING_FOR_ACCEPTANCE"))
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
                if(status) {
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
            }
        });
        assetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status) {
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
                            assetSelect = (Asset) customListViewSearchAssetAdapter.getItem(position);
                            tv_assetSelect.setText(assetSelect.getAssetName());
                            tv_asset.setText("Asset Code:" + assetSelect.getAssetCode());
                            Toast.makeText(EditAssignmentActivity.this, "" + assetSelect.getAssetCode(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgrsbarAssign.setVisibility(View.VISIBLE);
                Assignment assignment=new Assignment();
                //set list assignment
//                if(assetSelect.getAssetName()==null||assetSelect.getAssetName()==null)
//                {
//                    assignment.setAssetCode(assignmentSelect.getAssetCode());
//                    assignment.setAssetName(assignmentSelect.getAssetName());
//                    assignment.setSpecfication(assignmentSelect.getSpecfication());
//                }else
//                {
//                    assignment.setAssetCode(assetSelect.getAssetCode());
//                    assignment.setAssetName(assetSelect.getAssetName());
//                    assignment.setSpecfication(assetSelect.getSpecification());
//                }
                if(userSelect.getUsername()==null)
                {
                    assignment.setAssignedTo(assignmentSelect.getAssignedTo());
                }else
                {
                    assignment.setAssignedTo(userSelect.getUsername());
                }
                assignment.setNote(ed_note.getText().toString());

                assignment.setAssignedDate(ed_assignedDate.getText().toString());
                MainActivity.service.updateAssignment(assignment,assignmentSelect.getId().toString()).enqueue(new Callback<Assignment>() {
                    @Override
                    public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                        prgrsbarAssign.setVisibility(View.INVISIBLE);
                        if(response.code()==200)
                        {
                            MessageDialog.getInstance(EditAssignmentActivity.this, "Success",
                                    "Edit assignment success").show();
                        }else
                        {
                            if(response.code()==409)
                            {
                                MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                                        "Asset must available state!").show();
                            }else
                            {
                                MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                                        "Something went wrong").show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<Assignment> call, Throwable t) {
                        prgrsbarAssign.setVisibility(View.INVISIBLE);
                        MessageDialog.getInstance(EditAssignmentActivity.this, "Error",
                                "Something went wrong").show();
                    }
                });
            }
        });

    }



    private void load() {
        MainActivity.service.getAllUser().enqueue(new Callback<List<User>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    users.addAll(response.body().stream().filter(x->(!x.getStaffCode().equals(MainActivity.loginResponse.getStaffCode()))&&x.getState().equals("Enable")).collect(Collectors.toList()));
                    listSearchUser.addAll(response.body().stream().filter(x->(!x.getStaffCode().equals(MainActivity.loginResponse.getStaffCode()))&&x.getState().equals("Enable")).collect(Collectors.toList()));

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
                    assets.addAll(response.body());
                    listSearchAsset.addAll(response.body().stream().filter(x->{
                        return x.getState().equals("AVAILABLE");
                    }).collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {

            }
        });
    }
    public void selectAssignedDate(View v) {
        if(status)
        {
            builder.setTitleText("Select assigned date");
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            if(assignedDate!=0)
            {
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

    }
    private void setStatus(Boolean status)
    {
        ed_note.setEnabled(status);
        button_createAssign.setEnabled(status);
    }
    private void setData(Assignment data)
    {
        tv_userSelect.setText(data.getAssignedTo());
        tv_nameAssign.setText(data.getAssignedTo());
        ed_assignedDate.setText(data.getAssignedDate());
        tv_assignedDate.setText("Assigned Date: " + data.getAssignedDate());
        ed_note.setText(data.getNote());
        idAssignmentEdit.setText("No."+data.getId());
        List<String> temp=Arrays.asList(data.getAssignedDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        assignedDate=calendar.getTimeInMillis();
        switch (data.getState()) {
            case "ACCEPTED":
                state.setText("Accepted");
                state.setBackground(getResources().getDrawable(R.drawable.bg_green_status));
                break;
            case "WAITING_FOR_ACCEPTANCE":
                state.setText("Waiting for acceptance");
                state.setBackground(getResources().getDrawable(R.drawable.bg_yellow_status));
                break;
            case "WAITING_FOR_RETURNING":
                state.setText("Waiting for returning");
                state.setBackground(getResources().getDrawable(R.drawable.bg_orange_status));
                break;
            case "COMPLETED":
                state.setText("Completed");
                state.setBackground(getResources().getDrawable(R.drawable.bg_blue_status));
                break;
            case "CANCELED_ASSIGN":
                state.setText("Declined");
                state.setBackground(getResources().getDrawable(R.drawable.bg_red_status));
                break;
            default:
                break;
        }
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
            listSearchAsset.addAll(assets.stream().filter(x -> {
                return x.getAssetCode().toLowerCase().contains(key.toLowerCase()) || x.getAssetName().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            customListViewSearchAssetAdapter.notifyDataSetChanged();
        } else {
            listSearchAsset.addAll(assets);
            customListViewSearchAssetAdapter.notifyDataSetChanged();
        }
    }

}