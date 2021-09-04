package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.assets.Model.User;
import com.example.assets.R;

import java.util.List;

public class CustomListViewUserAdapter extends BaseAdapter {

    Context context;
    List<User> users;


    public CustomListViewUserAdapter(Context context, List<User> users) {
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
        ImageView imgHinh;
        TextView userCode, userFullName;
        TextView type,state;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list, parent, false);
        User user = (User) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();
        holder.imgHinh = convertView.findViewById(R.id.imageProfileComment);
        holder.userCode = convertView.findViewById(R.id.textName);
        holder.userFullName = convertView.findViewById(R.id.textContent);
        holder.type = convertView.findViewById(R.id.tv_type);
        holder.state = convertView.findViewById(R.id.tv_state);
        holder.userFullName.setText(user.getUsername());
        holder.imgHinh.setImageResource(R.drawable.ic_avatar);
        holder.userCode.setText(user.getFullName() + " (" + user.getStaffCode() + ")");
        holder.type.setText(user.getType().substring(5));
        switch (state(user.getState()))
        {
            case "Working":
            {
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_green_status));
                break;
            }
            default:
            {
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_red_status));
                break;
            }
        }
        holder.state.setText(state(user.getState()));
        convertView.setTag(holder);
        return convertView;
    }
    private String state(String s) {
        switch (s) {
            case "Enable":

                return "Working";
            case "Disabled":
                return "Break";
            default:
                return "";
        }
    }
}

