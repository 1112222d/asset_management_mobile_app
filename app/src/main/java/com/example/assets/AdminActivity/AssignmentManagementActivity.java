package com.example.assets.AdminActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.assets.Adapter.CustomListViewAssignmentAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignmentManagementActivity extends AppCompatActivity {
    ListView listView;
    LinearProgressIndicator indicator;
    CustomListViewAssignmentAdapter customListViewAssignmentAdapter;
    List<Assignment> listDataAssignment,listShowAssignment,listFilterAssignment;
    ImageView btn_create,backAssign;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Assignment assignmentNew=null;
    EditText ed_search;
    int item_select=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_management);
        listView=findViewById(R.id.assignListView);
        btn_create=findViewById(R.id.btn_createAssign);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        ed_search=findViewById(R.id.ed_search);
        ed_search.setHint("Search by id, name, code,assign to");
        indicator=findViewById(R.id.progressAssign);
        listDataAssignment=new ArrayList<>();
        listShowAssignment=new ArrayList<>();
        listFilterAssignment=new ArrayList<>();
        indicator.setProgressCompat(100, true);
        btn_create.setOnClickListener(v -> {
            Intent intent = new Intent(AssignmentManagementActivity.this,CreateNewAssignmentActivity.class);
            startActivity(intent);
        });
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(ed_search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        backAssign=findViewById(R.id.backAssign);
        backAssign.setOnClickListener(v->finish());
        loadAssignment();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indicator.setProgressCompat(100, true);
                loadAssignment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AssignmentManagementActivity.this,EditAssignmentActivity.class);
                intent.putExtra("assignment", (Serializable) customListViewAssignmentAdapter.getItem(position));
                startActivity(intent);
            }
        });

    }

    private void loadAssignment()
    {
        MainActivity.service.getAllAssignment().enqueue(new Callback<List<Assignment>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Assignment>> call, Response<List<Assignment>> response) {
                if(response.code()==200)
                {
                    listShowAssignment.clear();
                    listDataAssignment.clear();
                    listFilterAssignment.clear();
                    listDataAssignment.addAll(response.body());
                    listShowAssignment.addAll(response.body());
                    listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                    sort();
                    if(assignmentNew!=null)
                    {
                        listFilterAssignment=listFilterAssignment.stream().filter(x->x.getId()!=assignmentNew.getId()).collect(Collectors.toList());
                        listFilterAssignment.add(0,assignmentNew);
                        assignmentNew=null;
                    }
                    customListViewAssignmentAdapter = new CustomListViewAssignmentAdapter(getApplicationContext(),listFilterAssignment);
                    listView.setAdapter(customListViewAssignmentAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Assignment>> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAssignment();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String input)
    {
        listShowAssignment.clear();
        listFilterAssignment.clear();
        String key = input.replaceAll("\\s{2,}", " ").trim();
        if(!key.equals(""))
        {
            listShowAssignment.addAll(listDataAssignment.stream().filter(x->{
//                return x.getAssetName().toLowerCase().contains(key.toLowerCase())||x.getAssetCode().toLowerCase().contains(key.toLowerCase())||x.getId().toString().toLowerCase().contains(key.toLowerCase())||x.getAssignedTo().toLowerCase().contains(key.toLowerCase());
                return x.getId().toString().toLowerCase().contains(key.toLowerCase())||x.getAssignedTo().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            listFilterAssignment.addAll(filter(listShowAssignment,item_select));
            sort();
            customListViewAssignmentAdapter.notifyDataSetChanged();
        }else
        {
            listShowAssignment.addAll(listDataAssignment);
            listFilterAssignment.addAll(filter(listShowAssignment,item_select));
            sort();
            customListViewAssignmentAdapter.notifyDataSetChanged();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Assignment> filter(List<Assignment> list, int filter)
    {
        switch (filter) {
            case 0:
                return list;
            case 1:
                return list.stream().filter(x->x.getState().equals("ACCEPTED")).collect(Collectors.toList());
            case 2:
                return list.stream().filter(x->x.getState().equals("WAITING_FOR_ACCEPTANCE")).collect(Collectors.toList());
            case 3:
                return list.stream().filter(x->x.getState().equals("CANCELED_ASSIGN")).collect(Collectors.toList());
            case 4:
                return list.stream().filter(x->x.getState().equals("WAITING_FOR_RETURNING")).collect(Collectors.toList());
            case 5:
                return list.stream().filter(x->x.getState().equals("COMPLETED")).collect(Collectors.toList());
            default: {
                return list;
            }

        }
    }
    public void showAssign(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.assign_state_filter_menu, menu);
        MenuItem all = menu.findItem(R.id.ALL);
        MenuItem accepted = menu.findItem(R.id.ACCEPTED);
        MenuItem waiting = menu.findItem(R.id.WAITING_FOR_ACCEPTANCE);
        MenuItem cancel = menu.findItem(R.id.CANCELED_ASSIGN);
        MenuItem waitingReturn = menu.findItem(R.id.WAITING_FOR_RETURNING);
        MenuItem complete = menu.findItem(R.id.COMPLETED);
        switch (item_select) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                accepted.setChecked(true);
                break;
            case 2:
                waiting.setChecked(true);
                break;
            case 3:
                cancel.setChecked(true);
                break;
            case 4:
                waitingReturn.setChecked(true);
                break;
            case 5:
                complete.setChecked(true);
                break;
            default: {
                accepted.setChecked(false);
                waiting.setChecked(false);
                cancel.setChecked(false);
                waitingReturn.setChecked(false);
                complete.setChecked(false);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ALL:
                item.setChecked(true);
                item_select = 0;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;
            case R.id.ACCEPTED:
                item.setChecked(true);
                item_select = 1;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;

            case R.id.WAITING_FOR_ACCEPTANCE:
                item.setChecked(true);
                item_select = 2;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;

            case R.id.CANCELED_ASSIGN:
                item.setChecked(true);
                item_select = 3;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;
            case R.id.WAITING_FOR_RETURNING:
                item.setChecked(true);
                item_select = 4;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;
            case R.id.COMPLETED:
                item.setChecked(true);
                item_select = 5;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;
            default:
                return false;
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort()
    {
        Collections.sort(listFilterAssignment,(o1, o2)->
                o2.getAssignedDate().compareTo(o1.getAssignedDate())
        );
    }
}