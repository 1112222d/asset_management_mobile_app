package com.example.assets.UserActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.example.assets.Adapter.CustomListViewRequestAdapter;
import com.example.assets.MainActivity;
import com.example.assets.Model.Request;
import com.example.assets.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestActivity extends AppCompatActivity {

    ListView listView;
    LinearProgressIndicator indicator;
    List<Request> listDataRequest,listShowRequest,listFilterRequest;
    CustomListViewRequestAdapter customListViewRequestAdapter;
    ImageView backAssign;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText ed_search;
    int item_select=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_user);
        listView=findViewById(R.id.assetListView);
        ed_search=findViewById(R.id.ed_search);
        swipeRefreshLayout=findViewById(R.id.swiperefreshAsset);
        indicator=findViewById(R.id.progress);
        indicator.setProgressCompat(100, true);
        ed_search.setHint("Search by id,asset,request by");
        listDataRequest=new ArrayList<>();
        listFilterRequest=new ArrayList<>();
        listShowRequest=new ArrayList<>();

        backAssign=findViewById(R.id.backAsset);
        backAssign.setOnClickListener(v->finish());
        loadRequest();
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
                loadRequest();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

    }

    private void loadRequest()
    {
        MainActivity.service.getAllRequest().enqueue(new Callback<List<Request>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                if(response.code()==200)
                {
                    listDataRequest.clear();
                    listShowRequest.clear();
                    listFilterRequest.clear();
                    listShowRequest.addAll(response.body());
                    listDataRequest.addAll(response.body());
                    listFilterRequest.addAll(filter(listShowRequest,item_select));
                    customListViewRequestAdapter = new CustomListViewRequestAdapter(getApplicationContext(),listFilterRequest);
                    listView.setAdapter(customListViewRequestAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadRequest();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String key)
    {
        listShowRequest.clear();
        listFilterRequest.clear();
        if(!key.equals(""))
        {
            listShowRequest.addAll(listDataRequest.stream().filter(x->{
                return x.getAssetName().toLowerCase().contains(key.toLowerCase())||x.getAssetCode().toLowerCase().contains(key.toLowerCase())||x.getId().toString().toLowerCase().contains(key.toLowerCase())||x.getRequestBy().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            listFilterRequest.addAll(filter(listShowRequest,item_select));
            customListViewRequestAdapter.notifyDataSetChanged();
        }else
        {
            listShowRequest.addAll(listDataRequest);
            listFilterRequest.addAll(filter(listShowRequest,item_select));
            customListViewRequestAdapter.notifyDataSetChanged();
        }
    }

    public void showRequest(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.request_state_filter_menu, menu);
        MenuItem all = menu.findItem(R.id.ALL);
        MenuItem WAITING_FOR_RETURNING = menu.findItem(R.id.WAITING_FOR_RETURNING);
        MenuItem COMPLETED = menu.findItem(R.id.COMPLETED);

        switch (item_select) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                WAITING_FOR_RETURNING.setChecked(true);
                break;
            case 2:
                COMPLETED.setChecked(true);
                break;
            default: {
                all.setChecked(false);
                WAITING_FOR_RETURNING.setChecked(false);
                COMPLETED.setChecked(false);
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
                listFilterRequest.clear();
                listFilterRequest.addAll(filter(listShowRequest,item_select));
                customListViewRequestAdapter.notifyDataSetChanged();
                return true;
            case R.id.WAITING_FOR_RETURNING:
                item.setChecked(true);
                item_select = 1;
                listFilterRequest.clear();
                listFilterRequest.addAll(filter(listShowRequest,item_select));
                customListViewRequestAdapter.notifyDataSetChanged();
                return true;

            case R.id.COMPLETED:
                item.setChecked(true);
                item_select = 2;
                listFilterRequest.clear();
                listFilterRequest.addAll(filter(listShowRequest,item_select));
                customListViewRequestAdapter.notifyDataSetChanged();
                return true;

            default:
                return false;
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Request> filter(List<Request> list, int filter)
    {
        switch (filter) {
            case 1:
                return list.stream().filter(x->x.getState().equals("WAITING_FOR_RETURNING")).collect(Collectors.toList());
            case 2:
                return list.stream().filter(x->x.getState().equals("COMPLETED")).collect(Collectors.toList());
            default: {
                return list;
            }

        }
    }
}
