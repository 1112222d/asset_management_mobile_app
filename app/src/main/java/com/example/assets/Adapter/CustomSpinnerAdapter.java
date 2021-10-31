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
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.assets.AdminActivity.CreateNewAssetActivity;
import com.example.assets.AdminActivity.EditAssetActivity;
import com.example.assets.AlterDialog.Category_Dialog;
import com.example.assets.AlterDialog.Edit_Category_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Category;
import com.example.assets.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSpinnerAdapter extends BaseAdapter {

    List<Category> categories = new ArrayList<>();
    Context context;
    FragmentManager fm;
    @Override
    public int getCount() {
        return categories.size();
    }

    public CustomSpinnerAdapter(List<Category> categories,Context context) {
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
        TextView name, prefix;
        ImageView edit,cancel;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        Category cate = (Category) getItem(position);
        ViewHolder holder = new ViewHolder();
        holder.name=convertView.findViewById(R.id.tv_name);
        holder.prefix=convertView.findViewById(R.id.tv_prefix);
        holder.edit=convertView.findViewById(R.id.edit);
        holder.cancel=convertView.findViewById(R.id.cancel);
        holder.name.setText(cate.getName());
        holder.prefix.setText(cate.getPrefix());
        if(position==categories.size()-1)
        {
            holder.edit.setVisibility(View.INVISIBLE);
            holder.cancel.setVisibility(View.INVISIBLE);
        }else
        {
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
        }
        convertView.setTag(holder);
        return convertView;
    }
}
