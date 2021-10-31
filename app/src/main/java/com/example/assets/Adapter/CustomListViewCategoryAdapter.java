package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.assets.Model.Category;
import com.example.assets.Model.Department;
import com.example.assets.R;

import java.util.List;

public class CustomListViewCategoryAdapter extends BaseAdapter {

    Context context;
    List<Category> categories;


    public CustomListViewCategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;

    }

    @Override
    public int getCount() {
        return categories.size();
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
        TextView prefix, name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_user, parent, false);
        Category category = (Category) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.prefix = convertView.findViewById(R.id.userCode);
        holder.name = convertView.findViewById(R.id.nameUser);
        holder.prefix.setText(category.getPrefix());
        holder.name.setText(category.getName());
        convertView.setTag(holder);
        return convertView;
    }

}

