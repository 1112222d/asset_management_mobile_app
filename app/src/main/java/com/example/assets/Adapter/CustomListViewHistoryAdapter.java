package com.example.assets.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Request;
import com.example.assets.R;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomListViewHistoryAdapter extends BaseAdapter {

    Context context;
    List<Assignment> assignments;


    public CustomListViewHistoryAdapter(Context context, List<Assignment> assignments) {
        this.context = context;
        this.assignments = assignments;

    }

    @Override
    public int getCount() {
        return assignments.size();
    }

    @Override
    public Object getItem(int position) {
        return assignments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        EditText date,assignedTo,assignedBy,returnDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_history, parent, false);
        Assignment assignment = (Assignment) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        holder.date =  convertView.findViewById(R.id.date);
        holder.assignedTo = convertView.findViewById(R.id.assignedTo);
        holder.assignedBy = convertView.findViewById(R.id.assignedBy);
        holder.returnDate = convertView.findViewById(R.id.returnDate);

        holder.date.setText(assignment.getAssignedDate());
        holder.assignedTo.setText(assignment.getAssignedTo());
        holder.assignedBy.setText(assignment.getAssignedBy());
//        if(assignment.getReturnedDate()!=null)
//        {
//            holder.returnDate.setText(assignment.getReturnedDate().toString());
//        }
//        else
//        {
//            holder.returnDate.setText("");
//        }

        convertView.setTag(holder);
        return convertView;
    }
}

