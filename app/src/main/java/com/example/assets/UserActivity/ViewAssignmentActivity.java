package com.example.assets.UserActivity;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.Adapter.SearchModel;
import com.example.assets.AdminActivity.EditAssignmentActivity;
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

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAssignmentActivity extends AppCompatActivity {

    RelativeLayout userSearch, assetSearch,prgrsbarAssign;
    List<Asset> assets;
    List<User> users;
    User userSelect;
    Asset assetSelect;
    EditText ed_note;
    TextView tv_userSelect,tv_assetSelect,tv_assignedDate,ed_assignedDate,tv_nameAssign,tv_asset;
    Button button_accepted,button_cancel;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    long assignedDate;
    Assignment assignmentSelect;
    ImageView backBackAsset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment);
        userSearch = findViewById(R.id.userSearch);
        assetSearch = findViewById(R.id.assetSearch);
        assets = new ArrayList<>();
        users = new ArrayList<>();
        assetSelect=new Asset();
        userSelect=new User();
        tv_assetSelect=findViewById(R.id.TV_AssetSelect);
        tv_userSelect=findViewById(R.id.TV_UserSelect);
        tv_assignedDate=findViewById(R.id.tv_assignedDate);
        ed_assignedDate=findViewById(R.id.ed_assignedDate);
        ed_note=findViewById(R.id.ed_note);
        tv_nameAssign=findViewById(R.id.tv_nameAssign);
        tv_asset=findViewById(R.id.tv_asset);
        button_accepted=findViewById(R.id.button_Accepted);
        button_cancel=findViewById(R.id.button_cancel);
        prgrsbarAssign=findViewById(R.id.prgrsbarAssign);
        Intent intent = getIntent();

        assignmentSelect = (Assignment) intent.getSerializableExtra("assignment");
        backBackAsset=findViewById(R.id.backBackAsset);
        backBackAsset.setOnClickListener(v -> finish());
        setData(assignmentSelect);

        load();
        if(assignmentSelect.getState().equals("WAITING_FOR_ACCEPTANCE"))
        {
            button_accepted.setEnabled(true);
            button_cancel.setEnabled(true);
        }else
        {
            button_accepted.setEnabled(false);
            button_cancel.setEnabled(false);
        }
        button_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assignmentSelect.getState().equals("WAITING_FOR_ACCEPTANCE")) {
                    assignmentSelect.setState("ACCEPTED");
                    MessageDialog.getInstance(ViewAssignmentActivity.this, "Are you sure?",
                            "Do you want accept this assignment?").setPositiveButton("Appept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.service.changeStateStaffAssignment(assignmentSelect.getId(), assignmentSelect).enqueue(new Callback<Assignment>() {
                                @Override
                                public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(ViewAssignmentActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Assignment> call, Throwable t) {
                                    MessageDialog.getInstance(ViewAssignmentActivity.this, "Error", "Something went wrong").show();
                                }
                            });
                        }
                    }).setNegativeButton("Cancel",null).show();
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog2;
                if (assignmentSelect.getState().equals("WAITING_FOR_ACCEPTANCE")) {
                    dialog2 = new Dialog(ViewAssignmentActivity.this);
                    dialog2.setContentView(R.layout.layout_dialog_decline);
                    dialog2.getWindow().setLayout(1050, 1800);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    EditText reason;
                    Button ok, decline;
                    reason = dialog2.findViewById(R.id.decline_note);
                    ok = dialog2.findViewById(R.id.btn_ok);
                    decline = dialog2.findViewById(R.id.btn_decline);
                    dialog2.show();
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            assignmentSelect.setState("CANCELED_ASSIGN");
                            assignmentSelect.setNote(reason.getText().toString());
                            MainActivity.service.changeStateStaffAssignment(assignmentSelect.getId(), assignmentSelect).enqueue(new Callback<Assignment>() {
                                @Override
                                public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                                    if (response.code() == 200) {
                                        MessageDialog.getInstance(ViewAssignmentActivity.this, "Success", "Decline success").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog2.dismiss();
                                            }
                                        }).show();

                                    } else {
                                        MessageDialog.getInstance(ViewAssignmentActivity.this, "Error", "Decline Fail").show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Assignment> call, Throwable t) {
                                    MessageDialog.getInstance(ViewAssignmentActivity.this, "Error", "Decline Fail").show();

                                }
                            });
                        }
                    });
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });
                }}
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<SearchModel> initDataAsset()
    {
        ArrayList<SearchModel> items=new ArrayList<>();
        assets.stream().forEach(x->{
            if(x.getState().equals("AVAILABLE"))
            {
                items.add(new SearchModel(x.getAssetName()+" "+x.getAssetCode()));
            }
        });
        return items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private ArrayList<SearchModel> initDataUser()
    {
        ArrayList<SearchModel> items=new ArrayList<>();
        users.stream().forEach(x->{
            items.add(new SearchModel(x.getFullName()+" "+x.getStaffCode()));
        });
        return items;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chooseUser(String s)
    {
        List<String> list = Arrays.asList(s.split("\\s+"));
        String staffCode = list.get(list.size()-1);
        userSelect= users.stream().filter(x->x.getStaffCode().equals(staffCode)).findFirst().get();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chooseAsset(String s)
    {
        List<String> list = Arrays.asList(s.split("\\s+"));
        String assetCode = list.get(list.size()-1);
        assetSelect= assets.stream().filter(x->x.getAssetCode().equals(assetCode)).findFirst().get();
    }
    private void load() {
        MainActivity.service.getAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    users.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
        MainActivity.service.getAllAsset().enqueue(new Callback<List<Asset>>() {
            @Override
            public void onResponse(Call<List<Asset>> call, Response<List<Asset>> response) {
                if (response.code() == 200) {
                    assets.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {

            }
        });
    }

    private void setData(Assignment data)
    {
        tv_userSelect.setText(data.getAssignedTo());
        tv_nameAssign.setText(data.getAssignedTo());
        ed_assignedDate.setText(data.getAssignedDate());
        tv_assignedDate.setText("Assigned Date: " + data.getAssignedDate());
        ed_note.setText(data.getNote());
    }
}