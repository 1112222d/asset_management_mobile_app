package com.example.assets.AdminActivity;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assets.Adapter.CustomListViewSearchAssetAdapter;
import com.example.assets.Adapter.CustomListViewSearchUserAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewAssignmentAccepctActivity extends AppCompatActivity {
    RelativeLayout userSearch, assetSearch, prgrsbarAssign;
    List<Asset> assets;
    List<Asset> listSearchAsset;
    User users;
    Asset assetSelect;
    EditText ed_note;
    ImageView backBackAsset;
    TextView tv_userSelect, tv_assetSelect, tv_assignedDate, ed_assignedDate, tv_nameAssign, tv_asset;
    Button button_createAssign;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    CustomListViewSearchUserAdapter customListViewSearchUserAdapter;
    CustomListViewSearchAssetAdapter customListViewSearchAssetAdapter;
    Dialog dialog;
    final Calendar calendar = Calendar.getInstance();
    long assignedDate;
    AssignRequestRespone assignRequestRespone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_assignment);
        userSearch = findViewById(R.id.userSearch);
        assetSearch = findViewById(R.id.assetSearch);
        assets = new ArrayList<>();

        listSearchAsset = new ArrayList<>();
        tv_assetSelect = findViewById(R.id.TV_AssetSelect);
        tv_userSelect = findViewById(R.id.TV_UserSelect);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_assignedDate = findViewById(R.id.ed_assignedDate);
        ed_note = findViewById(R.id.ed_note);
        tv_nameAssign = findViewById(R.id.tv_nameAssign);
        tv_asset = findViewById(R.id.tv_asset);
        button_createAssign = findViewById(R.id.button_createAssign);
        prgrsbarAssign = findViewById(R.id.prgrsbarAssign);
        tv_assignedDate.setText("Assigned Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_assignedDate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        assignedDate = new Date().getTime();
        backBackAsset = findViewById(R.id.backBackAsset);
        backBackAsset.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        assignRequestRespone = (AssignRequestRespone) intent.getSerializableExtra("assignRequestRespone");
        load();
        assetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(CreateNewAssignmentAccepctActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_asset_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                EditText editText = dialog.findViewById(R.id.edit_text);
                ListView listView = dialog.findViewById(R.id.listView);
                customListViewSearchAssetAdapter = new CustomListViewSearchAssetAdapter(CreateNewAssignmentAccepctActivity.this, listSearchAsset);
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
                        tv_asset.setText("Asset:" + assetSelect.getAssetName());
                        Toast.makeText(CreateNewAssignmentAccepctActivity.this, "" + assetSelect.getAssetCode(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgrsbarAssign.setVisibility(View.VISIBLE);
                Boolean check = true;
                if (users == null) {
                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                            "Please choose user!").show();
                    check = false;
                }
                if (assetSelect == null) {
                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                            "Please choose asset!").show();
                    check = false;
                }
                if (ed_assignedDate.getText().toString().equals("dd/MM/yyyy")) {
                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                            "Please choose date!").show();
                    check = false;
                } else {
                    String message = checkDate(ed_assignedDate.getText().toString());
                    if (!message.equals("")) {
                        MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                                message).show();
                        check = false;
                    }
                }
//                if (ed_note.getText().toString().equals("")) {
//                    MessageDialog.getInstance(CreateNewAssignmentActivity.this, "Error",
//                            "Note must not blank!").show();
//                    check = false;
//                }
                if (check) {
                    Assignment assignment = new Assignment();
                    assignment.setAssetCode(assetSelect.getAssetCode());
                    assignment.setAssetName(assetSelect.getAssetName());
                    assignment.setAssignedTo(users.getUsername());
                    assignment.setNote(ed_note.getText().toString());
                    assignment.setSpecfication(assetSelect.getSpecification());
                    assignment.setAssignedDate(ed_assignedDate.getText().toString());
                    MainActivity.service.createAssignment(assignment).enqueue(new Callback<Assignment>() {
                        @Override
                        public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                            prgrsbarAssign.setVisibility(View.INVISIBLE);
                            if (response.code() == 200) {
                                MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Success",
                                        "Create assignment success").show();
                                AssignmentManagementActivity.assignmentNew = response.body();
                                accepted(assignRequestRespone);
                                load();
                                reload();

                            } else {
                                if (response.code() == 409) {
                                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                                            "Asset must available state!").show();
                                } else {
                                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
                                            "Something went wrong").show();
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<Assignment> call, Throwable t) {
                            prgrsbarAssign.setVisibility(View.INVISIBLE);
                            MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this, "Error",
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
        assetSelect = null;
        tv_assignedDate.setText("Assigned Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_assignedDate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_note.setText("");
        tv_userSelect.setText("User");
        tv_assetSelect.setText("Asset");
        tv_asset.setText("Asset:");
        tv_nameAssign.setText("New Assignment");
        assignedDate = new Date().getTime();
        calendar.clear();
    }

    private void load() {
        MainActivity.service.getAllUser().enqueue(new Callback<List<User>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.code() == 200) {
                    users=response.body().stream().filter(x -> (x.getState().equals("Enable"))&&x.getUsername().equals(assignRequestRespone.getRequestedBy())).findFirst().orElse(users=null);
                    if(users!=null)
                    {
                        tv_userSelect.setText(users.getFullName());

                    }else
                    {
                        button_createAssign.setEnabled(false);
                    }
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
//                if (response.code() == 200) {
//                    assets.addAll(response.body());
//                    listSearchAsset.addAll(response.body().stream().filter(x -> {
//                        return x.getState().equals("AVAILABLE")&&x.getCategoryPrefix().equals(assignRequestRespone.getPrefix());
//                    }).collect(Collectors.toList()));
//                }
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
    private void accepted(AssignRequestRespone assignRequestRespone)
    {
//        MainActivity.service.acceptedAssignRequest(assignRequestRespone.getId(),assignRequestRespone).enqueue(new Callback<AssignRequestRespone>() {
//            @Override
//            public void onResponse(Call<AssignRequestRespone> call, Response<AssignRequestRespone> response) {
//                if(response.code()==204)
//                {
//                    Toast.makeText(CreateNewAssignmentAccepctActivity.this, "OK", Toast.LENGTH_SHORT).show();
//                }else
//                {
//                    MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this,"Error","Accepted Fail").show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AssignRequestRespone> call, Throwable t) {
//                MessageDialog.getInstance(CreateNewAssignmentAccepctActivity.this,"Error","Accepted Fail").show();
//
//            }
//        });
    }
}