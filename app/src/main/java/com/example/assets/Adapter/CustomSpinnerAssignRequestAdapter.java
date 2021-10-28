package com.example.assets.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.assets.AdminActivity.CreateNewAssetActivity;
import com.example.assets.AdminActivity.EditAssetActivity;
import com.example.assets.AlterDialog.Edit_Category_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Category;
import com.example.assets.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSpinnerAssignRequestAdapter extends BaseAdapter {

    List<Category> categories = new ArrayList<>();
    Context context;
    FragmentManager fm;
    @Override
    public int getCount() {
        return categories.size();
    }

    public CustomSpinnerAssignRequestAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
        this.fm= ((FragmentActivity)context).getSupportFragmentManager();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder {
        TextView cateName, prefix;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_assignrequest, parent, false);
        Category cate = (Category) getItem(position);
        ViewHolder holder = new ViewHolder();
        holder.cateName=convertView.findViewById(R.id.cateName);

        holder.cateName.setText(cate.getName());

        convertView.setTag(holder);
        return convertView;
    }
}
