package com.example.assets.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.assets.AdminActivity.AssetManagementActivity;
import com.example.assets.AdminActivity.ViewAssignActivity;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.AssignRequestRespone;
import com.example.assets.Model.Assignment;
import com.example.assets.Model.Request;
import com.example.assets.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomListViewAssignmentUserAdapter extends BaseAdapter {

    Context context;
    List<Assignment> assignments;


    public CustomListViewAssignmentUserAdapter(Context context, List<Assignment> assignments) {
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
        LinearLayout check, cancel, refresh;
        ImageView im_check, im_cancel, im_refresh;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_assignment_user, parent, false);
        Assignment assignment = (Assignment) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        holder.idAssignment=convertView.findViewById(R.id.idAssignment);
        holder.assignTo =  convertView.findViewById(R.id.nameAssignTo);
        holder.assignBy = convertView.findViewById(R.id.nameAssignBy);
//        holder.assetName = convertView.findViewById(R.id.nameAsset);
        holder.state = convertView.findViewById(R.id.stateAssign);
        holder.date = convertView.findViewById(R.id.dateAssign);
        holder.check = convertView.findViewById(R.id.check);
        holder.cancel = convertView.findViewById(R.id.cancel);
        holder.refresh = convertView.findViewById(R.id.refresh);
        holder.im_check = convertView.findViewById(R.id.ic_check);
        holder.im_cancel = convertView.findViewById(R.id.ic_cancel);
        holder.im_refresh = convertView.findViewById(R.id.ic_refresh);

//        holder.assetName.setText(assignment.getAssetName());
        holder.assignTo.setText(assignment.getAssignedTo());
        holder.state.setText(state(assignment.getState()));
        holder.assignBy.setText(assignment.getAssignedBy());
        holder.date.setText(assignment.getAssignedDate());
        holder.idAssignment.setText(assignment.getId().toString());

        if(assignment.getState().equals("WAITING_FOR_ACCEPTANCE"))
        {
            holder.im_check.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_check));
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh_not_available));

//        }else if(assignment.getState().equals("ACCEPTED")&&!assignment.getCreatedRequest())
        }else if(assignment.getState().equals("ACCEPTED"))
        {
            holder.im_check.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_check_not_availalbe));
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel_not_available));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh));

        }else
        {
            holder.im_check.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_check_not_availalbe));
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel_not_available));
            holder.im_refresh.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_refresh_not_available));

        }
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (assignment.getState().equals("WAITING_FOR_ACCEPTANCE")) {
                    assignment.setState("ACCEPTED");
                    MessageDialog.getInstance(parent.getContext(), "Are you sure?",
                            "Do you want accept this assignment?").setPositiveButton("Appept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.service.changeStateStaffAssignment(assignment.getId(), assignment).enqueue(new Callback<Assignment>() {
                                @Override
                                public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                                    if (response.code() == 200) {
                                        Toast.makeText(parent.getContext(), "Success", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Assignment> call, Throwable t) {
                                    MessageDialog.getInstance(parent.getContext(), "Error", "Something went wrong").show();
                                }
                            });
                        }
                    }).setNegativeButton("Cancel",null).show();
                }
            }
        });
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog2;
                if (assignment.getState().equals("WAITING_FOR_ACCEPTANCE")) {
                    dialog2= new Dialog(parent.getContext());
                    dialog2.setContentView(R.layout.layout_dialog_decline);
                    dialog2.getWindow().setLayout(1050,1800);
                    dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    EditText reason;
                    Button ok, decline;
                    reason=dialog2.findViewById(R.id.decline_note);
                    ok=dialog2.findViewById(R.id.btn_ok);
                    decline=dialog2.findViewById(R.id.btn_decline);
                    dialog2.show();
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            assignment.setState("CANCELED_ASSIGN");
                            assignment.setNote(reason.getText().toString());
                            MainActivity.service.changeStateStaffAssignment(assignment.getId(),assignment).enqueue(new Callback<Assignment>() {
                                @Override
                                public void onResponse(Call<Assignment> call, Response<Assignment> response) {
                                    if(response.code()==200)
                                    {
                                        MessageDialog.getInstance(parent.getContext(), "Success","Decline success").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog2.dismiss();
                                            }
                                        }).show();

                                    }else
                                    {
                                        MessageDialog.getInstance(parent.getContext(),"Error","Decline Fail").show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Assignment> call, Throwable t) {
                                    MessageDialog.getInstance(parent.getContext(),"Error","Decline Fail").show();

                                }
                            });
                        }
                    });
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                        }
                    });
//                MessageDialog.getInstance(parent.getContext(), "Are you sure?",
//                        "Do you want decline this assignment?").setPositiveButton("Decline", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        MainActivity.service.changeStateStaffAssignment(assignment.getId(), "declined").enqueue(new Callback<Assignment>() {
//                            @RequiresApi(api = Build.VERSION_CODES.N)
//                            @Override
//                            public void onResponse(Call<Assignment> call, Response<Assignment> response) {
//                                if (response.code() == 200) {
//                                    Toast.makeText(parent.getContext(), "Success", Toast.LENGTH_SHORT).show();
//                                    assignments.remove(assignment);
//                                    notifyDataSetChanged();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Assignment> call, Throwable t) {
//                                MessageDialog.getInstance(parent.getContext(), "Error", "Something went wrong").show();
//                            }
//                        });
//                    }
//                }).setNegativeButton("Cancel",null).show();
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
//                    assignment.setCreatedRequest(true);
                    assignment.setState("WAITING_FOR_RETURNING");
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

