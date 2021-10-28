package com.example.assets.UserActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assets.Adapter.CustomSpinnerAssignRequestAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.AssignRequestEntity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Category;
import com.example.assets.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssignActivity extends AppCompatActivity {
    TextView nameCate, tv_assignedDate, tv_cate_name;
    Spinner ed_cate;
    EditText ed_note;
    CustomSpinnerAssignRequestAdapter customSpinnerAssignRequestAdapter;
    List<Category> categories;
    Category cateSelect;
    Button button_delete;
    AssignRequestRespone assignRequestRespone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignrequest);
        nameCate = findViewById(R.id.tv_nameAssign);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_cate = findViewById(R.id.ed_cate);
        ed_note = findViewById(R.id.ed_note);
        categories = new ArrayList<>();
        button_delete = findViewById(R.id.button_delete);
        customSpinnerAssignRequestAdapter = new CustomSpinnerAssignRequestAdapter(categories, EditAssignActivity.this);
        ed_cate.setAdapter(customSpinnerAssignRequestAdapter);
        tv_cate_name = findViewById(R.id.tv_asset);
        tv_assignedDate.setText("Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        Intent intent = getIntent();
        assignRequestRespone = (AssignRequestRespone) intent.getSerializableExtra("assignRequest");
        loadCate();

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.getInstance(EditAssignActivity.this,"Are you sure?","Do you want to delete this assign request ?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(assignRequestRespone.getState().equals("WAITING_FOR_ASSIGNING"))
                        {
                            MainActivity.service.deleteAssignRequest(assignRequestRespone.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code()==204)
                                    {
                                        Toast.makeText(EditAssignActivity.this, "Delete success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else
                                    {
                                        MessageDialog.getInstance(EditAssignActivity.this,"Error","Delete fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    MessageDialog.getInstance(EditAssignActivity.this,"Error","Delete fail");
                                }
                            });
                        }
                    }
                }).setNegativeButton("Cancel",null).show();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setData(AssignRequestRespone data) {
        ed_note.setText(data.getNote());
        tv_assignedDate.setText("Date: " + data.getRequestedDate());
        AtomicInteger index = new AtomicInteger();
        index.set(0);
        categories.stream().forEach(x -> {
            if (x.getPrefix().equals(data.getPrefix())) {
                ed_cate.setSelection(index.get());
                cateSelect = categories.get(index.get());
                tv_cate_name.setText("CATEGORY: " + cateSelect.getName());
            }
            index.set(index.get() + 1);
        });

        cateSelect = new Category(data.getCategory(), data.getPrefix());
        if (data.getState().equals("WAITING_FOR_ASSIGNING")) {
            button_delete.setEnabled(true);
        }else
        {
            button_delete.setEnabled(false);
        }
        ed_note.setEnabled(false);
        ed_cate.setEnabled(false);

    }

    private void loadCate() {
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    categories.clear();
                    categories.addAll(response.body());
                    customSpinnerAssignRequestAdapter.notifyDataSetChanged();
                    ed_cate.setSelection(0);
                    cateSelect = (Category) customSpinnerAssignRequestAdapter.getItem(0);
                    tv_cate_name.setText("CATEGORY: " + cateSelect.getName());
                    setData(assignRequestRespone);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }
}