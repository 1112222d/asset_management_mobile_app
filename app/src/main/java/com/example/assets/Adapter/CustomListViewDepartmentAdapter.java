package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.assets.Model.Department;
import com.example.assets.Model.User;
import com.example.assets.R;

import java.util.List;

public class CustomListViewDepartmentAdapter extends BaseAdapter {

    Context context;
    List<Department> departments;


    public CustomListViewDepartmentAdapter(Context context, List<Department> departments) {
        this.context = context;
        this.departments = departments;

    }

    @Override
    public int getCount() {
        return departments.size();
    }

    @Override
    public Object getItem(int position) {
        return departments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView deptCode, deptName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_user, parent, false);
        Department department = (Department) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.deptCode = convertView.findViewById(R.id.userCode);
        holder.deptName = convertView.findViewById(R.id.nameUser);
        holder.deptCode.setText(department.getDeptCode());
        holder.deptName.setText(department.getName());
        convertView.setTag(holder);
        return convertView;
    }

}

