package com.example.assets.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.assets.Model.Asset;
import com.example.assets.R;

import java.util.List;

public class CustomListViewSearchAssetAdapter extends BaseAdapter {

    Context context;
    List<Asset> assets;


    public CustomListViewSearchAssetAdapter(Context context, List<Asset> assets) {
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
        TextView assetCode, assetName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_user, parent, false);
        Asset asset = (Asset) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.assetCode = convertView.findViewById(R.id.userCode);
        holder.assetName = convertView.findViewById(R.id.nameUser);


        holder.assetCode.setText(asset.getAssetCode());
        holder.assetName.setText(asset.getAssetName());

        convertView.setTag(holder);

        return convertView;
    }

}

