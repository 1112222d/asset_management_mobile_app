package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.assets.Model.Asset;
import com.example.assets.Model.User;
import com.example.assets.R;

import java.util.List;

public class CustomListViewSearchUserAdapter extends BaseAdapter {

    Context context;
    List<User> users;


    public CustomListViewSearchUserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;

    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView assetCode, assetName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_user, parent, false);
        User user = (User) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.assetCode = convertView.findViewById(R.id.userCode);
        holder.assetName = convertView.findViewById(R.id.nameUser);


        holder.assetCode.setText(user.getStaffCode());
        holder.assetName.setText(user.getFullName());

        convertView.setTag(holder);

        return convertView;
    }

}

