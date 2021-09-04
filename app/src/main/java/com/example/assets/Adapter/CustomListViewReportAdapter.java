package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assets.Model.Report;
import com.example.assets.R;

import java.util.List;

public class CustomListViewReportAdapter extends BaseAdapter {
    Context context;
    List<Report> list;

    public CustomListViewReportAdapter(Context context, List<Report> list) {
        this.context = context;
        this.list = list;
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        TextView total, assigned, available, notAvailable, waitingForRecycled, recycled,tv_category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_report, parent, false);
        ViewHolder holder;
        holder = new ViewHolder();
        Report report = (Report) getItem(position);
        holder.total = convertView.findViewById(R.id.numberTotal);
        holder.tv_category=convertView.findViewById(R.id.tv_category);
        holder.assigned = convertView.findViewById(R.id.numberAssigned);
        holder.available = convertView.findViewById(R.id.numberAvailable);
        holder.notAvailable = convertView.findViewById(R.id.numberNotAvailable);
        holder.waitingForRecycled = convertView.findViewById(R.id.numberWaiting);
        holder.recycled = convertView.findViewById(R.id.numberRecycled);

        holder.total.setText(report.getTotal().toString());
        holder.assigned.setText(report.getAssigned().toString());
        holder.available.setText(report.getAvailable().toString());
        holder.notAvailable.setText(report.getNotAvailable().toString());
        holder.waitingForRecycled.setText(report.getWaitingForRecycle().toString());
        holder.recycled.setText(report.getRecycled().toString());
        holder.tv_category.setText(report.getCategory());
        convertView.setTag(holder);
        return convertView;
    }
}
