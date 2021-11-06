package com.example.assets.UserActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.assets.Adapter.CustomListViewCategoryAdapter;
import com.example.assets.Adapter.CustomListViewCategorySelectAdapter;
import com.example.assets.Adapter.CustomSpinnerAssignRequestAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.AssignRequestEntity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.AssignRequestRespone.RequestAssignDetailRS;

import com.example.assets.Model.Category;
import com.example.assets.Model.CategorySelect;
import com.example.assets.Model.RequestAssignDetail;
import com.example.assets.MyErrorMessage;
import com.example.assets.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssignActivity extends AppCompatActivity {
    TextView nameCate, tv_assignedDate, ed_cate, tv_StartDate, tv_DueDate, tv_available;
    ImageView add, refresh;
    EditText ed_number;
    List<Category> categories;
    Category cateSelect;
    Button button_createAssign;
    ListView lv_CateSelect;
    List<CategorySelect> listCategoriesSelect;
    Dialog dialog;
    public static CustomListViewCategoryAdapter customListViewCategoryAdapter;
    public static CustomListViewCategorySelectAdapter customListViewCategorySelectAdapter;
    MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
    MaterialDatePicker materialDatePicker;
    long startDate = 0, dueDate = 0;
    int quantityAvailableOfCategory = 0;
    final Calendar calendar = Calendar.getInstance();
    AssignRequestRespone respone= new AssignRequestRespone();
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        respone = (AssignRequestRespone) intent.getSerializableExtra("assignRequest");

        setContentView(R.layout.activity_create_new_assignrequest);
        nameCate = findViewById(R.id.tv_nameAssign);
        tv_assignedDate = findViewById(R.id.tv_assignedDate);
        tv_available = findViewById(R.id.tv_available);
        ed_cate = findViewById(R.id.ed_cate);
        ed_number = findViewById(R.id.ed_number);
        lv_CateSelect = findViewById(R.id.listview_CategorySelect);
        add = findViewById(R.id.add_category);
        refresh = findViewById(R.id.refresh_category);
        tv_StartDate = findViewById(R.id.date_start);
        tv_DueDate = findViewById(R.id.due_date);
        categories = new ArrayList<>();
        listCategoriesSelect = new ArrayList<>();
        setData(respone);
        respone.getRequestAssignDetails().forEach(x->{
            MainActivity.service.getSumOfAvailableAssetByCategory(x.getCategoryId(),DateFormat.format("yyyy-MM-dd", new Date(dueDate)).toString(),DateFormat.format("yyyy-MM-dd", new Date(startDate)).toString()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.code()==200)
                    {
                        listCategoriesSelect.add(new CategorySelect(x.getCategoryName(), x.getCategoryId(),x.getQuantity(), response.body()));
                        customListViewCategorySelectAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {

                }
            });

        });

        button_createAssign = findViewById(R.id.button_createAssign);
        customListViewCategoryAdapter = new CustomListViewCategoryAdapter(EditAssignActivity.this, categories);
        customListViewCategorySelectAdapter = new CustomListViewCategorySelectAdapter(EditAssignActivity.this, listCategoriesSelect);
        lv_CateSelect.setAdapter(customListViewCategorySelectAdapter);
        tv_assignedDate.setText("Date: " + DateFormat.format("dd/MM/yyyy", new Date()).toString());
        ed_number.setEnabled(false);
        ed_cate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCate();
                if (!tv_DueDate.getText().toString().equals("dd/MM/yyyy") && !tv_StartDate.getText().toString().equals("dd/MM/yyyy")) {
                    dialog = new Dialog(EditAssignActivity.this);
                    dialog.setContentView(R.layout.dialog_department_spinner);
                    dialog.getWindow().setLayout(800, 1800);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView title = dialog.findViewById(R.id.title);
                    ListView listView = dialog.findViewById(R.id.listView);
                    Button button = dialog.findViewById(R.id.create);
                    title.setText("Choose Category");
                    listView.setAdapter(customListViewCategoryAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            cateSelect = (Category) customListViewCategoryAdapter.getItem(position);
                            Toast.makeText(EditAssignActivity.this, "" + cateSelect.getName() + " " + cateSelect.getPrefix(), Toast.LENGTH_SHORT).show();
                            ed_cate.setText(cateSelect.getName());
                            ed_number.setEnabled(true);
                            MainActivity.service.getSumOfAvailableAssetByCategory(cateSelect.getPrefix(), DateFormat.format("yyyy-MM-dd", new Date(dueDate)).toString(), DateFormat.format("yyyy-MM-dd", new Date(startDate)).toString()).enqueue(new Callback<Integer>() {
                                @Override
                                public void onResponse(Call<Integer> call, Response<Integer> response) {
                                    quantityAvailableOfCategory = response.body();
                                    tv_available.setText(String.valueOf(quantityAvailableOfCategory));
                                }

                                @Override
                                public void onFailure(Call<Integer> call, Throwable t) {

                                }
                            });
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(EditAssignActivity.this, "Please input start date and due date", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_StartDate.setOnClickListener(v -> {
            if (check()) {
                MessageDialog.getInstance(EditAssignActivity.this, "Warning", "If you choose the date again, the category selected list will be removed").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Clear list and set date again
                        listCategoriesSelect.clear();
                        customListViewCategorySelectAdapter.notifyDataSetChanged();
                        refresh();
                        //finally open dialog select date
                        selectStartDate();

                    }
                }).setNegativeButton("Cancel", null).show();
            } else {
                selectStartDate();
            }
        });
        tv_DueDate.setOnClickListener(v -> {
            if (check()) {
                MessageDialog.getInstance(EditAssignActivity.this, "Warning", "If you choose the date again, the category selected list will be removed").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Clear list and set date again
                        listCategoriesSelect.clear();
                        customListViewCategorySelectAdapter.notifyDataSetChanged();
                        refresh();
                        //finally open dialog select date
                        selectDueDate();
                    }
                }).setNegativeButton("Cancel", null).show();
            } else {
                selectDueDate();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ed_number.getText().toString().equals("")) {
                    int number = Integer.parseInt(ed_number.getText().toString());
                    if (cateSelect != null && number > 0) {

                        //add category to list
                        if (number > quantityAvailableOfCategory)//check
                        {
                            //log error
                            MessageDialog.getInstance(EditAssignActivity.this, "Error", "The input number must be smaller than the available number").show();
                        } else {
                            //true add to select category select list
                            listCategoriesSelect.add(new CategorySelect(cateSelect, number, quantityAvailableOfCategory));
                            categories.remove(cateSelect);
                            refresh();
                            customListViewCategorySelectAdapter.notifyDataSetChanged();
                            customListViewCategoryAdapter.notifyDataSetChanged();
                        }
                        //remove categorySelect from categories
                    }
                } else {
                    MessageDialog.getInstance(EditAssignActivity.this, "Warning", "Please enter the number field").show();

                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDialog.getInstance(EditAssignActivity.this, "Warning", "Do you want to clear this form?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        refresh();
                        tv_DueDate.setText("dd/MM/yyyy");
                        tv_StartDate.setText("dd/MM/yyyy");
                        dueDate = 0;
                        startDate = 0;
                        listCategoriesSelect.clear();
                        customListViewCategorySelectAdapter.notifyDataSetChanged();
                    }
                }).setNegativeButton("No",null).show();
            }
        });
        button_createAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listCategoriesSelect.size()!=0)
                {
                    dialog = new Dialog(EditAssignActivity.this);
                    dialog.setContentView(R.layout.dialog_note);
                    dialog.getWindow().setLayout(1000, 800);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    EditText note = dialog.findViewById(R.id.note);
                    Button cancel,create;
                    cancel=dialog.findViewById(R.id.cancel);
                    create=dialog.findViewById(R.id.create);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    create.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View view) {
                            //create
                            List<RequestAssignDetail> list = new ArrayList<>();
                            listCategoriesSelect.forEach(x->{
                                RequestAssignDetail assignDetail = new RequestAssignDetail();
                                assignDetail.setCategoryId(x.getPrefix());
                                assignDetail.setQuantity(String.valueOf(x.getNumber()));
                                list.add(assignDetail);
                            });
                            AssignRequestEntity assignRequestEntity = new AssignRequestEntity();
                            assignRequestEntity.setRequestAssignDetails(list);
                            assignRequestEntity.setIntendedReturnDate(DateFormat.format("dd/MM/yyyy", new Date(dueDate)).toString());
                            assignRequestEntity.setIntendedAssignDate(DateFormat.format("dd/MM/yyyy", new Date(startDate)).toString());
                            assignRequestEntity.setNote(note.getText().toString());
                            MainActivity.service.createAssignRequest(assignRequestEntity).enqueue(new Callback<AssignRequestRespone>() {
                                @Override
                                public void onResponse(Call<AssignRequestRespone> call, Response<AssignRequestRespone> response) {
                                    if(response.code()==200)
                                    {
                                        MessageDialog.getInstance(EditAssignActivity.this, "Success", "Create assign request success").show();
                                    }else
                                    {
                                        Gson gson = new Gson();
                                        MyErrorMessage message = gson.fromJson(response.errorBody().charStream(), MyErrorMessage.class);
                                        MessageDialog.getInstance(EditAssignActivity.this, message.getError(),
                                                message.getMessage()).setPositiveButton("OK", (dialog, which) -> {
                                        }).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<AssignRequestRespone> call, Throwable t) {

                                }
                            });
                        }
                    });
                }else
                {
                    MessageDialog.getInstance(EditAssignActivity.this,"Error","Please select category !").show();
                }
            }
        });
    }

    private void setData(AssignRequestRespone data) {
        tv_StartDate.setText(data.getIntendedAssignDate());
        tv_DueDate.setText(data.getIntendedReturnDate());
        List<String> temp= Arrays.asList(data.getIntendedAssignDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        startDate=calendar.getTimeInMillis();
        temp=Arrays.asList(data.getIntendedReturnDate().split("/"));
        calendar.set(Integer.parseInt(temp.get(2)),Integer.parseInt(temp.get(1))-1,Integer.parseInt(temp.get(0)));
        dueDate=calendar.getTimeInMillis();
    }

    public void selectDueDate() {
        builder.setTitleText("Select your birthday");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        if (dueDate != 0) {
            calendar.setTime(new Date(dueDate));
            builder.setSelection(calendar.getTimeInMillis());
        }
        materialDatePicker = builder.build();
        calendar.clear();
        materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            dueDate = (long) selection;
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(dueDate)).toString();
            tv_DueDate.setText(dateString);
            refresh();
        });

    }

    public void selectStartDate() {
        builder.setTitleText("Select your birthday");
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        if (startDate != 0) {
            calendar.setTime(new Date(startDate));
            builder.setSelection(calendar.getTimeInMillis());

        }
        materialDatePicker = builder.build();
        calendar.clear();
        materialDatePicker.show(getSupportFragmentManager(), "Date_picker");
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            startDate = (long) selection;
            String dateString = DateFormat.format("dd/MM/yyyy", new Date(startDate)).toString();
            tv_StartDate.setText(dateString);
            refresh();
        });

    }

    private void refresh() {
        ed_number.setText("");
        cateSelect = null;
        ed_cate.setText("");
        tv_available.setText("0");
    }

    private boolean check() {
        if (listCategoriesSelect.size() != 0) {
            if (!tv_DueDate.getText().toString().equals("dd/MM/yyyy") && !tv_StartDate.getText().toString().equals("dd/MM/yyyy")) {
                return true;
            } else return false;
        } else return false;
    }

    private void loadCate() {
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.code() == 200) {
                    categories.clear();

                    List<Category> temp=new ArrayList<>();
                    for (Category x:response.body()) {
                        if(!listCategoriesSelect.stream().anyMatch(y->y.getPrefix().equals(x.getPrefix())))
                        {
                            temp.add(x) ;
                        }
                    }
                    categories.addAll(temp);
                    customListViewCategoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }
}