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
import android.widget.Toast;

import com.example.assets.Adapter.CustomListViewAdapter;
import com.example.assets.Adapter.CustomListViewUserAdapter;
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
import java.util.Map;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementActivity extends AppCompatActivity {
    ListView listView;
    LinearProgressIndicator indicator;
    CustomListViewUserAdapter customListViewUserAdapter;
    ImageView btnCreateUser;
    ImageView back;
    public static User userNew = null;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText ed_search;
    List<User> listDataUser, listShowUser, listFilterUser;
    int item_select = 0;
    int item_select2 = 0;

    @Override
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        listView = findViewById(R.id.userListView);
        btnCreateUser = findViewById(R.id.btn_createUser);
        indicator = findViewById(R.id.progressUser);
        indicator.setProgressCompat(100, true);
        back = findViewById(R.id.backUser);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                indicator.setProgressCompat(100, true);
                loadUser();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ed_search = findViewById(R.id.ed_search);
        ed_search.setHint("Search by staff code, name,username");
        listDataUser = new ArrayList<>();
        listShowUser = new ArrayList<>();
        listFilterUser = new ArrayList<>();
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
        loadUser();
        btnCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, CreateNewUserActivity.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> finish());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editUser((User) customListViewUserAdapter.getItem(position));
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.longclick);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit: {
                                editUser((User) customListViewUserAdapter.getItem(i));
                                return true;
                            }
                            case R.id.delete: {
                                deleteUser((User) customListViewUserAdapter.getItem(i));
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

    private void deleteUser(User user) {
        MessageDialog.getInstance(UserManagementActivity.this, "Are you sure",
                "Do you want to disable this user ? (" + user.getUsername() + ")").setPositiveButton("Disable", (dialog, which) -> {
            MainActivity.service.disableUser(user.getStaffCode()).enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.code() == 200) {
                        Toast.makeText(UserManagementActivity.this, "Disable success", Toast.LENGTH_SHORT).show();
                        indicator.setProgressCompat(100, true);
                        loadUser();
                    } else {
                        Toast.makeText(UserManagementActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable t) {
                    Toast.makeText(UserManagementActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            });
        }).setNegativeButton("Cancel", (dialog, which) -> {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }).show();
    }

    private void editUser(User user) {
        Intent intent = new Intent(UserManagementActivity.this, EditUserActivity.class);
        intent.putExtra("user", (Serializable) user);
        startActivity(intent);
        Toast.makeText(this, "Edit user" + user.getUsername(), Toast.LENGTH_SHORT).show();
    }

    private void loadUser() {
        MainActivity.service.getAllUser().enqueue(new Callback<List<User>>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                indicator.setProgressCompat(0, true);
                listDataUser.clear();
                listShowUser.clear();
                listFilterUser.clear();
                List<User> users = response.body();
                listDataUser.addAll(users);
                listShowUser.addAll(users);
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                if (userNew != null) {
                    listFilterUser = listFilterUser.stream().filter(x -> !x.getStaffCode().equals(userNew.getStaffCode())).collect(Collectors.toList());
                    listFilterUser.add(0, userNew);
                    userNew = null;
                }
                customListViewUserAdapter = new CustomListViewUserAdapter(getApplicationContext(), listFilterUser);
                listView.setAdapter(customListViewUserAdapter);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                MessageDialog.getInstance(UserManagementActivity.this, "Error",
                        "Something went wrong").show();
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void search(String input) {
        listShowUser.clear();
        listFilterUser.clear();
        String key = input.replaceAll("\\s{2,}", " ").trim();
        if (!key.equals("")) {
            listShowUser.addAll(listDataUser.stream().filter(x -> {
                return x.getFullName().toLowerCase().contains(key.toLowerCase()) || x.getStaffCode().toLowerCase().contains(key.toLowerCase()) || x.getUsername().toLowerCase().contains(key.toLowerCase());
            }).collect(Collectors.toList()));
            listFilterUser.addAll(filter(listShowUser, item_select));
            sort();
            customListViewUserAdapter.notifyDataSetChanged();
        } else {
            listShowUser.addAll(listDataUser);
            listFilterUser.addAll(filter(listShowUser, item_select));
            sort();
            customListViewUserAdapter.notifyDataSetChanged();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sort() {
        Collections.sort(listFilterUser, (o1, o2) ->
                o2.getJoinedDate().compareTo(o1.getJoinedDate())
        );
    }

    public void show(View v) {
        registerForContextMenu(v);
        openContextMenu(v);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_state, menu);
        MenuItem all = menu.findItem(R.id.all);
        MenuItem admin = menu.findItem(R.id.admin);
        MenuItem staff = menu.findItem(R.id.staff);
        MenuItem working = menu.findItem(R.id.working);
        MenuItem breakStatus = menu.findItem(R.id.breakstaus);
        switch (item_select) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                admin.setChecked(true);
                all.setChecked(false);
                break;
            case 2:
                staff.setChecked(true);
                all.setChecked(false);
                break;

            default: {
                all.setChecked(false);
                admin.setChecked(false);
                staff.setChecked(false);

            }

        }
        switch (item_select2) {
            case 0:
                all.setChecked(true);
                break;
            case 1:
                working.setChecked(true);
                break;
            case 2:
                breakStatus.setChecked(true);
                break;

            default: {
                all.setChecked(false);
                working.setChecked(false);
                breakStatus.setChecked(false);

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
                item_select2 = 0;
                listFilterUser.clear();
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                customListViewUserAdapter.notifyDataSetChanged();
                return true;
            case R.id.admin:
                item.setChecked(true);
                item_select = 1;
                listFilterUser.clear();
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                customListViewUserAdapter.notifyDataSetChanged();
                return true;

            case R.id.staff:
                item.setChecked(true);
                item_select = 2;
                listFilterUser.clear();
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                customListViewUserAdapter.notifyDataSetChanged();
                return true;
            case R.id.working:
                item.setChecked(true);
                item_select2 = 1;
                listFilterUser.clear();
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                customListViewUserAdapter.notifyDataSetChanged();
                return true;
            case R.id.breakstaus:
                item.setChecked(true);
                item_select2 = 2;
                listFilterUser.clear();
                listFilterUser.addAll(filter(listShowUser, item_select));
                sort();
                customListViewUserAdapter.notifyDataSetChanged();
                return true;

            default:
                return false;
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<User> filter(List<User> list, int filter) {
        List<User> users = new ArrayList<>();
        switch (filter) {
            case 1:
                users = list.stream().filter(x -> x.getType().equals("ROLE_ADMIN")).collect(Collectors.toList());
                return filterWorking(users, item_select2);
            case 2:
                users = list.stream().filter(x -> x.getType().equals("ROLE_STAFF")).collect(Collectors.toList());
                return filterWorking(users, item_select2);
            default: {
                return filterWorking(list, item_select2);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<User> filterWorking(List<User> list, int filter) {
        switch (filter) {
            case 1:
                return list.stream().filter(x -> x.getState().equals("Enable")).collect(Collectors.toList());
            case 2:
                return list.stream().filter(x -> x.getState().equals("Disabled")).collect(Collectors.toList());
            default: {
                return list;
            }

        }
    }
}