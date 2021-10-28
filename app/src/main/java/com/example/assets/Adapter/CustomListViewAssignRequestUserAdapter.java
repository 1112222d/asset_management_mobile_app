package com.example.assets.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Request;
import com.example.assets.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomListViewAssignRequestUserAdapter extends BaseAdapter {

    Context context;
    List<AssignRequestRespone> assignments;


    public CustomListViewAssignRequestUserAdapter(Context context, List<AssignRequestRespone> assignments) {
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
        TextView category,idAssignment;
        TextView state, date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_assign_request_user, parent, false);
        AssignRequestRespone assignment = (AssignRequestRespone) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        holder.idAssignment=convertView.findViewById(R.id.idAssignment);
        holder.category = convertView.findViewById(R.id.nameAsset);
        holder.state = convertView.findViewById(R.id.stateAssign);
        holder.date = convertView.findViewById(R.id.dateAssign);

        holder.state = convertView.findViewById(R.id.stateAssign);
        holder.category.setText(assignment.getCategory());
        holder.state.setText(state(assignment.getState()));
        holder.date.setText(assignment.getRequestedDate());
        holder.idAssignment.setText(assignment.getId().toString());



        switch (assignment.getState()) {
            case "ACCEPTED":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_green_status));
                break;
            case "WAITING_FOR_ASSIGNING":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_yellow_status));
                break;
            case "DECLINED":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_orange_status));
                break;
            default:
                break;
        }
        convertView.setTag(holder);
        return convertView;
    }

    private String state(String s) {
        switch (s) {
            case "ACCEPTED":
                return "Accepted";
            case "WAITING_FOR_ASSIGNING":
                return "Waiting for assigning";
            case "DECLINED":
                return "Declined";
            default:
                return "";
        }
    }
}

