package com.example.assets.AdminActivity;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.assets.Adapter.CustomListViewAssignRequestUserAdapter;
import com.example.assets.MainActivity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.R;
import com.example.assets.UserActivity.CreateNewAssignActivity;
import com.example.assets.UserActivity.EditAssignActivity;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestForAssigning extends AppCompatActivity {
    ListView listView;
    LinearProgressIndicator indicator;
    CustomListViewAssignRequestUserAdapter customListViewAssignmentAdapter;
    ImageView backAssign;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText ed_search;
    List<AssignRequestRespone> listDataAssignment,listShowAssignment,listFilterAssignment;
    int item_select=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_request);
        listView=findViewById(R.id.assignListView);
        swipeRefreshLayout=findViewById(R.id.swiperefresh);
        indicator=findViewById(R.id.progressAssign);
        indicator.setProgressCompat(100, true);
        ed_search = findViewById(R.id.ed_search);
        ed_search.setHint("Search by id, name, code,assign to");
        backAssign=findViewById(R.id.backAssign);
        backAssign.setOnClickListener(v->finish());
        listDataAssignment=new ArrayList<>();
        listShowAssignment=new ArrayList<>();
        listFilterAssignment=new ArrayList<>();
        customListViewAssignmentAdapter = new CustomListViewAssignRequestUserAdapter(getApplicationContext(),listFilterAssignment);
        loadAssignment();
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
                Intent intent = new Intent(RequestForAssigning.this, ViewAssignActivity.class);
                intent.putExtra("assignRequest", (Serializable) customListViewAssignmentAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    private void loadAssignment()
    {
        MainActivity.service.getAllAssignRequest().enqueue(new Callback<List<AssignRequestRespone>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<AssignRequestRespone>> call, Response<List<AssignRequestRespone>> response) {
                if(response.code()==200)
                {
                    listShowAssignment.clear();
                    listDataAssignment.clear();
                    listFilterAssignment.clear();
                    listDataAssignment.addAll(response.body());
                    listShowAssignment.addAll(response.body());
                    listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                    sort();
                    customListViewAssignmentAdapter = new CustomListViewAssignRequestUserAdapter(getApplicationContext(),listFilterAssignment);
                    listView.setAdapter(customListViewAssignmentAdapter);
                }else
                {
                    listFilterAssignment.clear();
                    customListViewAssignmentAdapter = new CustomListViewAssignRequestUserAdapter(getApplicationContext(),listFilterAssignment);
                    listView.setAdapter(customListViewAssignmentAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<AssignRequestRespone>> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAssignment();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String key)
    {
        listShowAssignment.clear();
        listFilterAssignment.clear();
        if(!key.equals(""))
        {
            listShowAssignment.addAll(listDataAssignment.stream().filter(x->{
                return x.getRequestedBy().toLowerCase().contains(key.toLowerCase())||x.getId().toString().toLowerCase().contains(key.toLowerCase());
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
    private List<AssignRequestRespone> filter(List<AssignRequestRespone> list, int filter)
    {
        switch (filter) {
            case 0:
                return list;
            case 1:
                return list.stream().filter(x->x.getState().equals("ACCEPTED")).collect(Collectors.toList());
            case 2:
                return list.stream().filter(x->x.getState().equals("WAITING_FOR_ASSIGNING")).collect(Collectors.toList());
            case 3:
                return list.stream().filter(x->x.getState().equals("DECLINED")).collect(Collectors.toList());
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
        menuInflater.inflate(R.menu.assign_request_state_filter_menu, menu);
        MenuItem all = menu.findItem(R.id.ALL);
        MenuItem accepted = menu.findItem(R.id.ACCEPTED);
        MenuItem waiting = menu.findItem(R.id.WAITING_FOR_ASSIGNING);
        MenuItem cancel = menu.findItem(R.id.DECLINED);

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
            default: {
                accepted.setChecked(false);
                waiting.setChecked(false);
                cancel.setChecked(false);
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

            case R.id.WAITING_FOR_ASSIGNING:
                item.setChecked(true);
                item_select = 2;
                listFilterAssignment.clear();
                listFilterAssignment.addAll(filter(listShowAssignment,item_select));
                sort();
                customListViewAssignmentAdapter.notifyDataSetChanged();
                return true;

            case R.id.DECLINED:
                item.setChecked(true);
                item_select = 3;
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
                o2.getRequestedDate().compareTo(o1.getRequestedDate())
        );
    }

}