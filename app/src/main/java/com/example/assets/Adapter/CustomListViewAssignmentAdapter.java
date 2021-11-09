package com.example.assets.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.AdminActivity.AssignmentManagementActivity;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Request;
import com.example.assets.Model.User;
import com.example.assets.R;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomListViewAssignmentAdapter extends BaseAdapter {

    Context context;
    List<Assignment> assignments;


    public CustomListViewAssignmentAdapter(Context context, List<Assignment> assignments) {
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
        TextView  assignTo, assignBy,idAssignment;
        TextView state, date;
        LinearLayout cancel, refresh;
        ImageView  im_cancel, im_refresh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_assignment, parent, false);
        Assignment assignment = (Assignment) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        holder.assignTo =  convertView.findViewById(R.id.nameAssignTo);
        holder.assignBy = convertView.findViewById(R.id.nameAssignBy);
//        holder.assetName = convertView.findViewById(R.id.nameAsset);
        holder.state = convertView.findViewById(R.id.stateAssign);
        holder.date = convertView.findViewById(R.id.dateAssign);
        holder.idAssignment=convertView.findViewById(R.id.idAssignment);


        holder.cancel = convertView.findViewById(R.id.cancel);
        holder.refresh = convertView.findViewById(R.id.refresh);

        holder.im_cancel = convertView.findViewById(R.id.ic_cancel);
        holder.im_refresh = convertView.findViewById(R.id.ic_refresh);

//        holder.assetName.setText(assignment.getAssetName());
        holder.assignTo.setText(assignment.getAssignedTo());
        holder.state.setText(state(assignment.getState()));
        holder.assignBy.setText(assignment.getAssignedBy());
        holder.date.setText(assignment.getAssignedDate());
        holder.idAssignment.setText("No."+assignment.getId().toString());
        if(assignment.getState().equals("WAITING_FOR_ACCEPTANCE"))
        {
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh_not_available));

//        }else if(assignment.getState().equals("ACCEPTED")&&!assignment.getCreatedRequest())
        }else if(assignment.getState().equals("ACCEPTED"))
        {

            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel_not_available));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh));

        }else
        {
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel_not_available));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh_not_available));

        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assignment.getState().equals("WAITING_FOR_ACCEPTANCE")||assignment.getState().equals("COMPLETED")) {
                    MessageDialog.getInstance(parent.getContext(), "Are you sure?",
                            "Do you want delete this assignment?").setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAssign(assignment,parent.getContext());
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }}
        });
        holder.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (assignment.getState().equals("ACCEPTED")&&!assignment.getCreatedRequest()) {
                if (assignment.getState().equals("ACCEPTED")) {
                    MessageDialog.getInstance(parent.getContext(), "Are you sure?",
                            "Do you want to create returning request this asset?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            createReturn(assignment,parent.getContext());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }}
        });
        switch (assignment.getState()) {
            case "ACCEPTED":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_green_status));
                break;
            case "WAITING_FOR_ACCEPTANCE":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_yellow_status));
                break;
            case "WAITING_FOR_RETURNING":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_orange_status));
                break;
            case "COMPLETED":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_blue_status));
                break;
            case "CANCELED_ASSIGN":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_red_status));
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
            case "WAITING_FOR_ACCEPTANCE":
                return "Waiting for acceptance";
            case "WAITING_FOR_RETURNING":
                return "Waiting for returning";
            case "COMPLETED":
                return "Completed";
            case "CANCELED_ASSIGN":
                return "Declined";
            default:
                return "";
        }
    }
    private void deleteAssign(Assignment assignment,Context context)
    {
        MainActivity.service.deleteAssignment(assignment.getId().toString()).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> response) {
                if(response.code()==200)
                {
                    MessageDialog.getInstance(context, "Success",
                            "Delete assignment success").show();
                    assignments.remove(assignment);
                    notifyDataSetChanged();
                }else
                {
                    MessageDialog.getInstance(context, "Error",
                            "Something went wrong").show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {
                MessageDialog.getInstance(context, "Error",
                        "Something went wrong").show();
            }
        });
    }
    private void createReturn(Assignment assignment,Context context)
    {
        Request request = new Request();
        request.setAssignmentId(Integer.parseInt(assignment.getId().toString()));
        MainActivity.service.createRequest(request).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if(response.code()==200)
                {
                    MessageDialog.getInstance(context,"Success","Create returning asset success").show();
                    assignment.setState("WAITING_FOR_RETURNING");
//                    assignment.setCreatedRequest(true);
                    notifyDataSetChanged();
                }else
                {
                    MessageDialog.getInstance(context,"Error","Something went wrong").show();
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                MessageDialog.getInstance(context,"Error","Something went wrong").show();
            }
        });
    }
}

