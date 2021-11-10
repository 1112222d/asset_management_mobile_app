package com.example.assets.AdminActivity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.Adapter.CustomListViewCategoryAdapter;
import com.example.assets.Adapter.CustomListViewHistoryAdapter;
import com.example.assets.Adapter.CustomSpinnerAdapter;
import com.example.assets.AlterDialog.Category_Dialog;
import com.example.assets.AlterDialog.Edit_Category_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Category;
import com.example.assets.MyErrorMessage;
import com.example.assets.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssetActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    EditText editName, editSepc;
    TextView name, category, installDate, editInstallDate, state, idAssetEdit,edCate;
    List<Category> categories;
    Boolean status = false;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    long installedDate;
    Button button, delete;
    public static Category cateSelect;
    String stateSelect = "AVAILABLE";
    RelativeLayout progress;
    Asset asset;
    ImageView editAsset, back,history;
    Dialog dialog;
    @Override
    protected void onStart() {
        super.onStart();
        loadCate();
    }
    public CustomListViewCategoryAdapter customListViewCategoryAdapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        asset = (Asset) intent.getSerializableExtra("asset");
        setContentView(R.layout.activity_edit_asset);
        editName = findViewById(R.id.ed_nameEdit);
        editSepc = findViewById(R.id.ed_specEdit);
        name = findViewById(R.id.tv_nameAssetEdit);
        category = findViewById(R.id.tv_cateEdit);
        history=findViewById(R.id.history);
        installDate = findViewById(R.id.tv_installDateEdit);
        editInstallDate = findViewById(R.id.ed_installedDateEdit);
        state = findViewById(R.id.ed_stateEdit);
        edCate = findViewById(R.id.ed_cate);
        categories = new ArrayList<>();
        customListViewCategoryAdapter=new CustomListViewCategoryAdapter(EditAssetActivity.this,categories);
        cateSelect = new Category();
        button = findViewById(R.id.button_EditAsset);
        progress = findViewById(R.id.prgrsbarEditAsset);
        editAsset = findViewById(R.id.editAsset);
        delete = findViewById(R.id.button_DeleteAsset);
        idAssetEdit = findViewById(R.id.idAssetEdit);

        back = findViewById(R.id.backBackAssetEdit);
        back.setOnClickListener(v -> finish());
        editName.setEnabled(status);
        editSepc.setEnabled(status);
        button.setEnabled(status);
        setData(asset);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog= new Dialog(EditAssetActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_history_spinner);
                dialog.getWindow().setLayout(1050,1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ListView listView = dialog.findViewById(R.id.listView);
                dialog.show();
                CustomListViewHistoryAdapter adapter = new CustomListViewHistoryAdapter(EditAssetActivity.this,asset.getAssignmentDTOs());
                listView.setAdapter(adapter);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.getInstance(EditAssetActivity.this, "Are you sure?",
                        "Do you want delete this asset ?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.service.deleteAsset(asset.getAssetCode()).enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if (response.code() == 200) {
                                    MessageDialog.getInstance(EditAssetActivity.this, "Success",
                                            "Delete success").show();
                                    finish();
                                } else {
                                    Gson gson = new Gson();
                                    MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                                    MessageDialog.getInstance(EditAssetActivity.this, message.getError(),
                                            message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                                    }).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                MessageDialog.getInstance(EditAssetActivity.this, "Error",
                                        "Something went wrong").show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

            }
        });
        editAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asset.getState().equals("ASSIGNED")) {
                    Toast.makeText(EditAssetActivity.this, "Can't edit,Because the asset is already assigned", Toast.LENGTH_SHORT).show();
                } else {
                    status = !status;
                    setStatus(status);
                    Toast.makeText(EditAssetActivity.this, "Edit mode"+ (status?"ON":"OFF"), Toast.LENGTH_SHORT).show();

                }
            }
        });
        edCate.setOnClickListener(v->{
            Toast.makeText(EditAssetActivity.this, "Can't edit category", Toast.LENGTH_SHORT).show();
        });
        button.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            Boolean check = true;
            if (editName.getText().toString().equals("")) {
                MessageDialog.getInstance(EditAssetActivity.this, "Error",
                        "Name must not blank").show();
                check = false;
            }
            if (editSepc.getText().toString().equals("")) {
                MessageDialog.getInstance(EditAssetActivity.this, "Error",
                        "Specification must not blank").show();
                check = false;
            }

            if (editInstallDate.getText().toString().equals("dd/MM/yyyy")) {
                MessageDialog.getInstance(EditAssetActivity.this, "Error",
                        "Installed Date must not blank").show();
                check = false;
            }
            if (check == true) {
                MainActivity.service.updateAsset(new Asset(editName.getText().toString(),
                        stateSelect,
                        editSepc.getText().toString(),
                        editInstallDate.getText().toString(),
                        cateSelect.getPrefix(),
                        cateSelect.getName()
                ), asset.getAssetCode()).enqueue(new Callback<Asset>() {
                    @Override
                    public void onResponse(Call<Asset> call, Response<Asset> response) {
                        progress.setVisibility(View.INVISIBLE);
                        if (response.code() == 200) {
                            MessageDialog.getInstance(EditAssetActivity.this, "Success",
                                    "Edit asset success").show();
                        }else {
                            Gson gson = new Gson();
                            MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                            MessageDialog.getInstance(EditAssetActivity.this, message.getError(),
                                    message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                            }).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Asset> call, Throwable t) {

                    }
                });
            } else {
                progress.setVisibility(View.INVISIBLE);
            }
        });

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name.setText(editName.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editInstallDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInstalled(v, status);
            }
        });
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectState(v, status);
            }
        });

    }


    public void selectState(View v, Boolean status) {
        if (status == true) {

            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.asseteditstate);
            popup.show();
        }
    }

    public void selectInstalled(View v, Boolean status) {
        if (status == true) {
            builder.setTitleText("Select your joined date");
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            if (installedDate != 0) {
//                calendar.setTime(new Date(installedDate));
                calendar.setTimeInMillis(installedDate);
                builder.setSelection(installedDate);
            }
            materialDatePicker = builder.build();
            calendar.clear();
            materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                installedDate = (long) selection;
                String dateString = DateFormat.format("dd/MM/yyyy", new Date(installedDate)).toString();
                editInstallDate.setText(dateString);
                installDate.setText("Installed Date: " + dateString);

            });
        }

    }

    private void loadCate(){
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.code()==200)
                {
                    categories.clear();
                    categories.addAll(response.body());
                    customListViewCategoryAdapter.notifyDataSetChanged();
                    edCate.setText(categories.get(0).getName());
                    cateSelect= categories.get(0);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.available:
                state.setText("Available");
                stateSelect = "AVAILABLE";
                return true;
            case R.id.notAvailable:
                state.setText("Not available");
                stateSelect = "NOT_AVAILABLE";
                return true;
            case R.id.waitingForRecycling:
                state.setText("Waiting for recycling");
                stateSelect = "WAITING_FOR_RECYCLING";
                return true;
            case R.id.recycled:
                state.setText("Recycled");
                stateSelect = "RECYCLED";
                return true;
            default:
                return false;
        }
    }

    public void openDialog() {
        Category_Dialog custom_dialog = new Category_Dialog(categories);
        custom_dialog.show(getSupportFragmentManager(), "Create category");
        customListViewCategoryAdapter.notifyDataSetChanged();
    }

    private void setStatus(Boolean status) {

        editName.setEnabled(status);
        editSepc.setEnabled(status);
        button.setEnabled(status);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setData(Asset data) {
        editName.setText(data.getAssetName());
        editSepc.setText(data.getSpecification());

        editInstallDate.setText(data.getInstalledDate());
        name.setText(data.getAssetName());
        List<String> temp = Arrays.asList(data.getInstalledDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)), Integer.parseInt(temp.get(1)) - 1, Integer.parseInt(temp.get(0)));
        installedDate = calendar.getTimeInMillis();
        installDate.setText("Installed Date: " + data.getInstalledDate());
        idAssetEdit.setText("ID: " + data.getAssetCode());
        if (data.getState().equals("AVAILABLE")) {
            state.setText("Available");
            stateSelect = "AVAILABLE";
        }
        if (data.getState().equals("NOT_AVAILABLE")) {
            state.setText("Not available");
            stateSelect = "NOT_AVAILABLE";
        }
        if (data.getState().equals("WAITING_FOR_RECYCLING")) {
            state.setText("Waiting for recycling");
            stateSelect = "WAITING_FOR_RECYCLING";
        }
        if (data.getState().equals("RECYCLED")) {
            state.setText("Recycled");
            stateSelect = "RECYCLED";
        }
        if (data.getState().equals("ASSIGNED")) {
            state.setText("Assigned");
            stateSelect = "ASSIGNED";
        }
        AtomicInteger index = new AtomicInteger();
        index.set(0);
        categories.stream().forEach(x -> {
            if (x.getPrefix().equals(data.getCategoryPrefix())) {
                cateSelect = categories.get(index.get());
                category.setText("CATEGORY: " + cateSelect.getName());
            }
            index.set(index.get() + 1);
        });

        cateSelect = new Category(data.getCategoryName(), data.getCategoryPrefix());
    }
}