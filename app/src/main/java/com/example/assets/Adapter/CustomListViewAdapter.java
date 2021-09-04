package com.example.assets.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.assets.Model.Asset;
import com.example.assets.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomListViewAdapter extends BaseAdapter {

    Context context;
    List<Asset> assets;


    public CustomListViewAdapter(Context context, List<Asset> assets) {
        this.context = context;
        this.assets = assets;

    }

    @Override
    public int getCount() {
        return assets.size();
    }

    @Override
    public Object getItem(int position) {
        return assets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    private class ViewHolder {
        TextView assetCode, assetName,state;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_asset, parent, false);
        Asset asset = (Asset) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.assetCode = convertView.findViewById(R.id.textNameLV);
        holder.assetName = convertView.findViewById(R.id.textContentLV);

        holder.state=convertView.findViewById(R.id.state);
        holder.state.setText(state(asset.getState()));

        holder.assetName.setText(asset.getAssetName());
        holder.assetCode.setText(asset.getAssetCode());

        switch (asset.getState())
        {
            case "AVAILABLE":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_green_status));
                break;
            case "NOT_AVAILABLE":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_gray_status));
                break;
            case "WAITING_FOR_RECYCLING":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_yellow_status));
                break;
            case "RECYCLED":
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_blue_status));
                break;
            default:
                holder.state.setBackground(convertView.getResources().getDrawable(R.drawable.bg_orange_status));
                break;
        }
        View finalConvertView = convertView;
        convertView = finalConvertView;
        convertView.setTag(holder);

        return convertView;
    }
    private String state(String s)
    {
        switch (s)
        {
            case "AVAILABLE":
                return "Available";
            case "NOT_AVAILABLE":
                return "Not available";
            case "WAITING_FOR_RECYCLING":
                return "Waiting for recycling";
            case "RECYCLED":
                return "Recycled";
            default:
                return "Assigned";
        }
    }
}

