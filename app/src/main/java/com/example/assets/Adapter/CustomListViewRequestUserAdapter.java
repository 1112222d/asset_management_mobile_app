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

import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Request;
import com.example.assets.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomListViewRequestUserAdapter extends BaseAdapter {

    Context context;
    List<Request> requests;


    public CustomListViewRequestUserAdapter(Context context, List<Request> requests) {
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_request_user, parent, false);
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
        holder.assetName.setText(request.getAssetName());
        holder.requestBy.setText(request.getRequestBy());
        holder.state.setText(state(request.getState()));
        holder.acceptedBy.setText(request.getAcceptBy());
        holder.dateRe.setText(request.getRequestedDate());
        holder.dateAc.setText(request.getReturnedDate());
        holder.idRequest.setText(request.getId().toString());
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
}

