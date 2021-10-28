package com.example.assets.AdminActivity;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assets.Adapter.CustomListViewHistoryAdapter;
import com.example.assets.Adapter.CustomSpinnerAssignRequestAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Category;
import com.example.assets.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAssignActivity extends AppCompatActivity {
    TextView nameCate, tv_assignedDate, tv_cate_name,tv_nameAssign;
    Spinner ed_cate;
    EditText ed_note;
    CustomSpinnerAssignRequestAdapter customSpinnerAssignRequestAdapter;
    List<Category> categories;
    Category cateSelect;
    Button button_cancel, button_accepted;
    AssignRequestRespone assignRequestRespone;
    Dialog dialog2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_detail_assignrequest);
        nameCate = findViewById(R.id.tv_nameAssign);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_cate = findViewById(R.id.ed_cate);
        ed_note = findViewById(R.id.ed_note);
        categories = new ArrayList<>();
        button_cancel = findViewById(R.id.button_cancel);
        button_accepted = findViewById(R.id.button_accepted);
        customSpinnerAssignRequestAdapter = new CustomSpinnerAssignRequestAdapter(categories, ViewAssignActivity.this);
        ed_cate.setAdapter(customSpinnerAssignRequestAdapter);
        tv_cate_name = findViewById(R.id.tv_asset);
        tv_nameAssign=findViewById(R.id.tv_nameAssign);
        tv_assignedDate.setText("Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        Intent intent = getIntent();
        assignRequestRespone = (AssignRequestRespone) intent.getSerializableExtra("assignRequest");
        loadCate();

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2= new Dialog(ViewAssignActivity.this);
                dialog2.setContentView(R.layout.dialog_decline_assign_request);
                dialog2.getWindow().setLayout(1050,1800);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText reason;
                Button ok, decline;
                reason=dialog2.findViewById(R.id.decline_note);
                ok=dialog2.findViewById(R.id.btn_ok);
                decline=dialog2.findViewById(R.id.btn_decline);
                dialog2.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assignRequestRespone.setState("DECLINED");
                        assignRequestRespone.setNote(reason.getText().toString());
                        MainActivity.service.acceptedAssignRequest(assignRequestRespone.getId(),assignRequestRespone).enqueue(new Callback<AssignRequestRespone>() {
                            @Override
                            public void onResponse(Call<AssignRequestRespone> call, Response<AssignRequestRespone> response) {
                                if(response.code()==200)
                                {
                                    MessageDialog.getInstance(ViewAssignActivity.this,"Success","Decline success").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog2.dismiss();
                                        }
                                    }).show();

                                }else
                                {
                                    MessageDialog.getInstance(ViewAssignActivity.this,"Error","Decline Fail").show();

                                }
                            }

                            @Override
                            public void onFailure(Call<AssignRequestRespone> call, Throwable t) {
                                MessageDialog.getInstance(ViewAssignActivity.this,"Error","Decline Fail").show();

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


            }


        });
        button_accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            assignRequestRespone.setState("ACCEPTED");
            Intent intent1 = new Intent(ViewAssignActivity.this,CreateNewAssignmentAccepctActivity.class);
            intent1.putExtra("assignRequestRespone", (Serializable) assignRequestRespone);
            startActivity(intent1);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setData(AssignRequestRespone data) {
        ed_note.setText(data.getNote());
        tv_nameAssign.setText("Request by: "+data.getRequestedBy());
        tv_assignedDate.setText("Date: " + data.getRequestedDate());
        AtomicInteger index = new AtomicInteger();
        index.set(0);
        ed_note.setEnabled(false);
        ed_cate.setEnabled(false);
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
            button_accepted.setEnabled(true);
            button_cancel.setEnabled(true);
        } else {

            button_cancel.setEnabled(false);
            button_accepted.setEnabled(false);
        }
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