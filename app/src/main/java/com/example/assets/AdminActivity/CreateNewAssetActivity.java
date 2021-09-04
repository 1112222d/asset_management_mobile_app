package com.example.assets.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.Adapter.CustomSpinnerAdapter;
import com.example.assets.AlterDialog.Category_Dialog;
import com.example.assets.AlterDialog.Custom_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Category;
import com.example.assets.R;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewAssetActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{

    EditText editName, editSepc;
    TextView name, category, installDate, editInstallDate, state;
    Spinner edCate;
    public static CustomSpinnerAdapter customSpinnerAdapter;
    List<Category> categories;

    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    final Calendar calendar = Calendar.getInstance();
    long installedDate=0;
    Button button;
    public static Category cateSelect;
    String stateSelect="AVAILABLE";
    RelativeLayout progress;
    ImageView back;

    @Override
    protected void onStart() {
        super.onStart();
        loadCate();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_asset);
        editName=findViewById(R.id.ed_name);
        editSepc=findViewById(R.id.ed_spec);
        name=findViewById(R.id.tv_nameAsset);
        category=findViewById(R.id.tv_cate);
        installDate=findViewById(R.id.tv_installDate);
        editInstallDate=findViewById(R.id.ed_installedDate);
        state=findViewById(R.id.ed_state);
        edCate=findViewById(R.id.ed_cate);

        categories= new ArrayList<>();
        customSpinnerAdapter=new CustomSpinnerAdapter(categories,CreateNewAssetActivity.this);
        cateSelect=new Category();
        button = findViewById(R.id.button_createAsset);
        progress=findViewById(R.id.prgrsbarAsset);
        back = findViewById(R.id.backBackAsset);
        installDate.setText("Installed Date: "+DateFormat.format("dd/MM/yyyy", new Date()).toString());
        editInstallDate.setText(DateFormat.format("dd/MM/yyyy", new Date()).toString());
        installedDate=new Date().getTime();
        back.setOnClickListener(v -> finish());
        button.setOnClickListener(v -> {
            progress.setVisibility(View.VISIBLE);
            Boolean check = true;
            if(editName.getText().toString().equals(""))
            {
                MessageDialog.getInstance(CreateNewAssetActivity.this, "Error",
                        "Name must not blank").show();
                check=false;
            }
            if(editSepc.getText().toString().equals(""))
            {
                MessageDialog.getInstance(CreateNewAssetActivity.this, "Error",
                        "Specification must not blank").show();
                check=false;
            }

            if (editInstallDate.getText().toString().equals("dd/MM/yyyy")) {
                MessageDialog.getInstance(CreateNewAssetActivity.this, "Error",
                        "Installed Date must not blank").show();
                check = false;
            }
            if(check==true)
            {
                MainActivity.service.createAsset(new Asset(editName.getText().toString(),
                        stateSelect,
                        editSepc.getText().toString(),
                        editInstallDate.getText().toString(),
                        cateSelect.getPrefix(),
                        cateSelect.getName()
                        )).enqueue(new Callback<Asset>() {
                    @Override
                    public void onResponse(Call<Asset> call, Response<Asset> response) {
                        progress.setVisibility(View.INVISIBLE);
                        if(response.code()==200)
                        {
                            MessageDialog.getInstance(CreateNewAssetActivity.this, "Success",
                                    "Create asset success").show();
                            AssetManagementActivity.assetNew=response.body();
                        }
                    }

                    @Override
                    public void onFailure(Call<Asset> call, Throwable t) {

                    }
                });
            }else
            {
                progress.setVisibility(View.INVISIBLE);
            }
        });
        edCate.setAdapter(customSpinnerAdapter);
        edCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(customSpinnerAdapter.getCount()-1==position)
                {
                    openDialog();

                }else
                {
                    cateSelect=(Category)customSpinnerAdapter.getItem(position);
                    category.setText("CATEGORY: "+cateSelect.getName());

                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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



    }
    public void selectState(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.assetstate);
        popup.show();
    }
    public void selectInstalled(View v) {
        builder.setTitleText("Select your joined date");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        if(installedDate!=0)
        {
            calendar.setTime(new Date(installedDate));
            builder.setSelection(calendar.getTimeInMillis());
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
    private void loadCate(){
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.code()==200)
                {
                    categories.clear();
                    categories.addAll(response.body());
                    categories.add(new Category("Create new",""));
                    customSpinnerAdapter.notifyDataSetChanged();
                    edCate.setSelection(0);
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
                stateSelect="AVAILABLE";
                return true;
            case R.id.notAvailable:
                state.setText("Not available");
                stateSelect="NOT_AVAILABLE";
                return true;
            default:
                return false;
        }
    }

    public void openDialog() {
        Category_Dialog custom_dialog = new Category_Dialog(categories);
        custom_dialog.show(getSupportFragmentManager(), "Create category");

    }
}