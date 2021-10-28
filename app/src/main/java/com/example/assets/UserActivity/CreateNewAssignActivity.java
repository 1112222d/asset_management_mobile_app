package com.example.assets.UserActivity;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assets.Adapter.CustomListViewSearchAssetAdapter;
import com.example.assets.Adapter.CustomListViewSearchUserAdapter;
import com.example.assets.Adapter.CustomSpinnerAdapter;
import com.example.assets.Adapter.CustomSpinnerAssignRequestAdapter;
import com.example.assets.AdminActivity.AssignmentManagementActivity;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.AssignRequestEntity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Category;
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

public class CreateNewAssignActivity extends AppCompatActivity {
    TextView nameCate, tv_assignedDate, tv_cate_name;
    Spinner ed_cate;
    EditText ed_note;
    CustomSpinnerAssignRequestAdapter customSpinnerAssignRequestAdapter;
    List<Category> categories;
    Category cateSelect;
    Button button_createAssign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_assignrequest);
        nameCate = findViewById(R.id.tv_nameAssign);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_cate = findViewById(R.id.ed_cate);
        ed_note = findViewById(R.id.ed_note);
        categories = new ArrayList<>();
        button_createAssign = findViewById(R.id.button_createAssign);
        customSpinnerAssignRequestAdapter = new CustomSpinnerAssignRequestAdapter(categories, CreateNewAssignActivity.this);
        ed_cate.setAdapter(customSpinnerAssignRequestAdapter);
        tv_cate_name = findViewById(R.id.tv_asset);
        tv_assignedDate.setText("Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());

        loadCate();
        ed_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cateSelect = (Category) customSpinnerAssignRequestAdapter.getItem(position);
                tv_cate_name.setText("CATEGORY: " + cateSelect.getName());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed_note.getText().toString().trim().equals(""))
                {
                    MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Note must not blank").show();
                }else
                {
                    MainActivity.service.createAssignRequest(new AssignRequestEntity(cateSelect.getPrefix(),ed_note.getText().toString())).enqueue(new Callback<AssignRequestRespone>() {
                        @Override
                        public void onResponse(Call<AssignRequestRespone> call, Response<AssignRequestRespone> response) {
                            if(response.code()==200)
                            {
                                MessageDialog.getInstance(CreateNewAssignActivity.this,"Success","Create assign request success").show();
                            }else
                            {
                                MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Create assign request fail").show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AssignRequestRespone> call, Throwable t) {
                            MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Create assign request fail").show();
                        }
                    });
                }
            }
        });
    }

    private void loadCate() {
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    categories.clear();
                    categories.addAll(response.body());
                    customSpinnerAssignRequestAdapter.notifyDataSetChanged();
                    ed_cate.setSelection(0);
                    cateSelect = (Category) customSpinnerAssignRequestAdapter.getItem(0);
                    tv_cate_name.setText("CATEGORY: " + cateSelect.getName());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }
}