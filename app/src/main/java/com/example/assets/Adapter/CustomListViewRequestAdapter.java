package com.example.assets.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.http.HttpResponseCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.assets.AdminActivity.AssetManagementActivity;
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

public class CustomListViewRequestAdapter extends BaseAdapter {

    Context context;
    List<Request> requests;


    public CustomListViewRequestAdapter(Context context, List<Request> requests) {
        this.context = context;
        this.requests = requests;

    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView assetName, requestBy, acceptedBy,idRequest;
        TextView state, dateRe,dateAc;
        LinearLayout check, cancel;
        ImageView  im_check, im_cancel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_request, parent, false);
        Request request = (Request) getItem(position);

        ViewHolder holder;
        holder = new ViewHolder();

        holder.idRequest=convertView.findViewById(R.id.idRequest);
        holder.requestBy =  convertView.findViewById(R.id.requestBy);
        holder.acceptedBy = convertView.findViewById(R.id.acceptedBy);
        holder.assetName = convertView.findViewById(R.id.nameAsset);
        holder.state = convertView.findViewById(R.id.stateRequest);
        holder.dateRe = convertView.findViewById(R.id.dateRequest);
        holder.dateAc = convertView.findViewById(R.id.dateAccepted);

        holder.cancel = convertView.findViewById(R.id.cancel);
        holder.check = convertView.findViewById(R.id.check);

        holder.im_cancel = convertView.findViewById(R.id.ic_cancel);
        holder.im_check = convertView.findViewById(R.id.ic_check);

        holder.assetName.setText(request.getAssetName());
        holder.requestBy.setText(request.getRequestBy());
        holder.state.setText(state(request.getState()));
        holder.acceptedBy.setText(request.getAcceptBy());
        holder.dateRe.setText(request.getRequestedDate());
        holder.dateAc.setText(request.getReturnedDate());
        holder.idRequest.setText(request.getId().toString());
        if(request.getState().equals("WAITING_FOR_RETURNING"))
        {
            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel));
            holder.im_check.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_check));

        }else if(request.getState().equals("COMPLETED"))
        {

            holder.im_cancel.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_cancel_not_available));
            holder.im_check.setImageDrawable(convertView.getResources().getDrawable(R.drawable.ic_check_not_availalbe));

        }

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request.getState().equals("WAITING_FOR_RETURNING")) {
                    MessageDialog.getInstance(parent.getContext(), "Are you sure?",
                            "Do you want to cancel this returning request?").setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelRequest(request,parent.getContext());
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }}
        });
        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (request.getState().equals("WAITING_FOR_RETURNING")) {
                    MessageDialog.getInstance(parent.getContext(), "Are you sure?",
                            "Do you want to mark this returning request as 'Completed'?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            completeRequest(request,parent.getContext());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                }}
        });
        switch (request.getState()) {
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
                return "Cancel";
            default:
                return "";
        }
    }
    private void cancelRequest(Request request,Context context)
    {
        MainActivity.service.cancelRequest(request.getId().longValue()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.code()==204)
                {
                    MessageDialog.getInstance(context, "Success",
                            "Cancel request success").show();
                    requests.remove(request);
                    notifyDataSetChanged();
                }else
                {
                    MessageDialog.getInstance(context, "Error",
                            "Something went wrong").show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                MessageDialog.getInstance(context, "Error",
                        "Something went wrong").show();
            }
        });
    }
    private void completeRequest(Request request,Context context)
    {
        MainActivity.service.acceptRequest(request.getId().longValue()).enqueue(new Callback<Request>() {
            @Override
            public void onResponse(Call<Request> call, Response<Request> response) {
                if(response.code()==200)
                {
                    MessageDialog.getInstance(context, "Success",
                            "Accepted request success").show();
                    requests.remove(request);
                    requests.add(0,response.body());
                    notifyDataSetChanged();
                }else
                {
                    MessageDialog.getInstance(context, "Error",
                            "Something went wrong").show();
                }
            }

            @Override
            public void onFailure(Call<Request> call, Throwable t) {
                MessageDialog.getInstance(context, "Error",
                        "Something went wrong").show();
            }
        });
    }
}

