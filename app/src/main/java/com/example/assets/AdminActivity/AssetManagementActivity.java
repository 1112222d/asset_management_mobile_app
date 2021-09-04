package com.example.assets.AdminActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.assets.Adapter.CustomListViewAdapter;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetManagementActivity extends AppCompatActivity {
    ListView listView;
    LinearProgressIndicator indicator;
    CustomListViewAdapter customListViewAdapter;
    ImageView back;
    List<Asset> listDataAsset, listSearchAsset,listFilterAsset;
    ImageView btnCreateAsset;
    SwipeRefreshLayout swipeRefreshLayout;
    public static Asset assetNew=null;
    EditText ed_search;
    int item_select = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_management);
        listView = findViewById(R.id.assetListView);
        indicator = findViewById(R.id.progress);
        indicator.setProgressCompat(100, true);
        back = findViewById(R.id.backAsset);
        swipeRefreshLayout = findViewById(R.id.swiperefreshAsset);
        btnCreateAsset = findViewById(R.id.btn_createAsset);
        ed_search = findViewById(R.id.ed_search);
        ed_search.setHint("Search by asset name or asset code");
        listDataAsset = new ArrayList<>();
        listSearchAsset = new ArrayList<>();
        listFilterAsset = new ArrayList<>();
        loadAsset();
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
        back.setOnClickListener(v -> finish());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indicator.setProgressCompat(100, true);
                loadAsset();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        btnCreateAsset.setOnClickListener(v -> {
            Intent intent = new Intent(AssetManagementActivity.this, CreateNewAssetActivity.class);
            startActivity(intent);
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editAsset((Asset) customListViewAdapter.getItem(position));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.longclick);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit: {
                                editAsset((Asset) customListViewAdapter.getItem(position));
                                return true;
                            }
                            case R.id.delete: {
                                deleteAsset((Asset) customListViewAdapter.getItem(position));
                                return true;
                            }
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                return true;
            }
        });

    }

    private void editAsset(Asset asset) {
        Intent intent = new Intent(AssetManagementActivity.this, EditAssetActivity.class);
        intent.putExtra("asset", (Serializable) asset);
        startActivity(intent);
    }

    private void deleteAsset(Asset asset) {
        MessageDialog.getInstance(AssetManagementActivity.this, "Are you sure?",
                "Do you want delete this asset ?").setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.service.deleteAsset(asset.getAssetCode()).enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.code() == 200) {
                            MessageDialog.getInstance(AssetManagementActivity.this, "Success",
                                    "Delete success").show();
                            loadAsset();
                        } else {
                            Toast.makeText(AssetManagementActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        MessageDialog.getInstance(AssetManagementActivity.this, "Error",
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

    @Override
    protected void onResume() {
        super.onResume();
        loadAsset();
    }

    public void loadAsset() {

        MainActivity.service.getAllAsset().enqueue(new Callback<List<Asset>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<Asset>> call, Response<List<Asset>> response) {
                indicator.setProgressCompat(0, true);
                if (response.code() == 200) {
                    listSearchAsset.clear();
                    listDataAsset.clear();
                    listFilterAsset.clear();
                    listDataAsset.addAll(response.body());
                    listSearchAsset.addAll(response.body());
                    listFilterAsset.addAll(filter(listSearchAsset,item_select));
                    sort();
                    if(assetNew!=null)
                    {
                        listFilterAsset=listFilterAsset.stream().filter(x->!x.getAssetCode().equals(assetNew.getAssetCode())).collect(Collectors.toList());
                        listFilterAsset.add(0,assetNew);
                        assetNew=null;
                    }
                    customListViewAdapter = new CustomListViewAdapter(getApplicationContext(), listFilterAsset);
                    listView.setAdapter(customListViewAdapter);
                }
                if (response.code() == 204) {
                    Toast.makeText(AssetManagementActivity.this, "Please create asset", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String input) {
        listSearchAsset.clear();
        listFilterAsset.clear();
        String key = input.replaceAll("\\s{2,}", " ").trim();
        if (!key.equals("")) {
            listSearchAsset.addAll(listDataAsset.stream().filter(x -> {
                return x.getAssetName().toLowerCase().contains(key.toLowerCase()) || x.getAssetCode().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            listFilterAsset.addAll(filter(listSearchAsset,item_select));
            sort();
            customListViewAdapter.notifyDataSetChanged();
        } else {
            listSearchAsset.addAll(listDataAsset);
            listFilterAsset.addAll(filter(listSearchAsset,item_select));
            sort();
            customListViewAdapter.notifyDataSetChanged();
        }
    }


    public void show(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort()
    {
        Collections.sort(listFilterAsset,(o1,o2)->
                o2.getInstalledDate().compareTo(o1.getInstalledDate())
        );
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.asset_state_filter_menu, menu);
        MenuItem all = menu.findItem(R.id.all);
        MenuItem available = menu.findItem(R.id.availableState);
        MenuItem not_available = menu.findItem(R.id.not_availableState);
        MenuItem assigned = menu.findItem(R.id.assigned);
        MenuItem waiting = menu.findItem(R.id.waiting);
        MenuItem recycled = menu.findItem(R.id.recycled);
        switch (item_select) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                available.setChecked(true);
                break;
            case 2:
                not_available.setChecked(true);
                break;
            case 3:
                assigned.setChecked(true);
                break;
            case 4:
                waiting.setChecked(true);
                break;
            case 5:
                recycled.setChecked(true);
                break;
            default: {
                all.setChecked(false);
                available.setChecked(false);
                not_available.setChecked(false);
                assigned.setChecked(false);
                waiting.setChecked(false);
                recycled.setChecked(false);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
                item.setChecked(true);
                item_select = 0;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;
            case R.id.availableState:
                item.setChecked(true);
                item_select = 1;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.not_availableState:
                item.setChecked(true);
                item_select = 2;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.assigned:
                item.setChecked(true);
                item_select = 3;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.waiting:
                item.setChecked(true);
                item_select = 4;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;

            case R.id.recycled:
                item.setChecked(true);
                item_select = 5;
                listFilterAsset.clear();
                listFilterAsset.addAll(filter(listSearchAsset,item_select));
                sort();
                customListViewAdapter.notifyDataSetChanged();
                return true;

            default:
                return false;
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Asset> filter(List<Asset> list, int filter)
    {
        switch (filter) {
            case 1:
                return list.stream().filter(x->x.getState().equals("AVAILABLE")).collect(Collectors.toList());
            case 2:
                return list.stream().filter(x->x.getState().equals("NOT_AVAILABLE")).collect(Collectors.toList());
            case 3:
                return list.stream().filter(x->x.getState().equals("ASSIGNED")).collect(Collectors.toList());
            case 4:
                return list.stream().filter(x->x.getState().equals("WAITING_FOR_RECYCLING")).collect(Collectors.toList());
            case 5:
                return list.stream().filter(x->x.getState().equals("RECYCLED")).collect(Collectors.toList());
            default: {
                return list;
            }

        }
    }

}