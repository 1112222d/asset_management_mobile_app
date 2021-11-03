package com.example.assets.UserActivity;

import android.app.Dialog;
import android.content.DialogInterface;
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

import com.example.assets.Adapter.CustomListViewCategoryAdapter;
import com.example.assets.Adapter.CustomListViewSearchAssetAdapter;
import com.example.assets.Adapter.CustomListViewSearchUserAdapter;
import com.example.assets.Adapter.CustomSpinnerAdapter;
import com.example.assets.Adapter.CustomSpinnerAssignRequestAdapter;
import com.example.assets.AdminActivity.AssignmentManagementActivity;
import com.example.assets.AdminActivity.CreateNewAssetActivity;
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
    TextView nameCate, tv_assignedDate,ed_cate,tv_StartDate,tv_DueDate;
    ImageView add,refresh;
    EditText ed_number;
    List<Category> categories;
    Category cateSelect;
    Button button_createAssign;
    ListView lv_CateSelect;
    List<Category> listCategoriesSelect;
    Dialog dialog;
    public static CustomListViewCategoryAdapter customListViewCategoryAdapter;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    long startDate=0, dueDate=0;
    final Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_assignrequest);
        nameCate = findViewById(R.id.tv_nameAssign);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        ed_cate = findViewById(R.id.ed_cate);
        ed_number = findViewById(R.id.ed_number);
        lv_CateSelect=findViewById(R.id.listview_CategorySelect);
        add=findViewById(R.id.add_category);
        refresh=findViewById(R.id.refresh_category);
        tv_StartDate=findViewById(R.id.date_start);
        tv_DueDate=findViewById(R.id.due_date);
        categories = new ArrayList<>();
        listCategoriesSelect= new ArrayList<>();
        button_createAssign = findViewById(R.id.button_createAssign);
        customListViewCategoryAdapter=new CustomListViewCategoryAdapter(CreateNewAssignActivity.this,categories);
        tv_assignedDate.setText("Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());

        loadCate();
        ed_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(CreateNewAssignActivity.this);
                dialog.setContentView(R.layout.dialog_department_spinner);
                dialog.getWindow().setLayout(800, 1800);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView title =dialog.findViewById(R.id.title);
                ListView listView = dialog.findViewById(R.id.listView);
                Button button = dialog.findViewById(R.id.create);
                title.setText("Choose Category");
                listView.setAdapter(customListViewCategoryAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        cateSelect = (Category) customListViewCategoryAdapter.getItem(position);
                        Toast.makeText(CreateNewAssignActivity.this, "" + cateSelect.getName()+" "+cateSelect.getPrefix(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        tv_StartDate.setOnClickListener(v->{
            if(check())
            {
                MessageDialog.getInstance(CreateNewAssignActivity.this,"Warning","If you choose the date again, the category selected list will be removed").setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Clear list and set date again
                    }
                }).setPositiveButton("Cancel",null).show();
            }
        });
        tv_DueDate.setOnClickListener(v->{

        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cateSelect!=null&&Integer.parseInt(ed_number.getText().toString())>0)
                {
                    //add category to list
                    //remove category from categorySelect
                }
            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(ed_note.getText().toString().trim().equals(""))
//                {
//                    MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Note must not blank").show();
//                }else
//                {
//                    MainActivity.service.createAssignRequest(new AssignRequestEntity(cateSelect.getPrefix(),ed_note.getText().toString())).enqueue(new Callback<AssignRequestRespone>() {
//                        @Override
//                        public void onResponse(Call<AssignRequestRespone> call, Response<AssignRequestRespone> response) {
//                            if(response.code()==200)
//                            {
//                                MessageDialog.getInstance(CreateNewAssignActivity.this,"Success","Create assign request success").show();
//                            }else
//                            {
//                                MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Create assign request fail").show();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<AssignRequestRespone> call, Throwable t) {
//                            MessageDialog.getInstance(CreateNewAssignActivity.this,"Error","Create assign request fail").show();
//                        }
//                    });
//                }
            }
        });
    }

    private boolean check() {
        if(listCategoriesSelect.size()!=0)
        {
            if(!tv_DueDate.getText().toString().equals("dd/MM/yyyy")&&!tv_StartDate.getText().toString().equals("dd/MM/yyyy"))
            {
                return true;
            }else return false;
        }else return false;
    }

    private void loadCate() {
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    categories.clear();
                    categories.addAll(response.body().stream().filter(
                            x->listCategoriesSelect.stream().anyMatch(y->y.getPrefix()==x.getPrefix())
                    ).collect(Collectors.toList()));
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

}