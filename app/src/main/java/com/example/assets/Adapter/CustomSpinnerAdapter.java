package com.example.assets.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.assets.AdminActivity.CreateNewAssetActivity;
import com.example.assets.AdminActivity.EditAssetActivity;
import com.example.assets.AlterDialog.Category_Dialog;
import com.example.assets.AlterDialog.Edit_Category_Dialog;
import com.example.assets.AlterDialog.MessageDialog;
import com.example.assets.MainActivity;
import com.example.assets.Model.Asset;
import com.example.assets.Model.Category;
import com.example.assets.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomSpinnerAdapter extends BaseAdapter {

    List<Category> categories = new ArrayList<>();
    Context context;
    FragmentManager fm;
    @Override
    public int getCount() {
        return categories.size();
    }

    public CustomSpinnerAdapter(List<Category> categories,Context context) {
        this.categories = categories;
        this.context = context;
        this.fm= ((FragmentActivity)context).getSupportFragmentManager();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder {
        TextView cateName, prefix;
        ImageView edit,cancel;
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        Category cate = (Category) getItem(position);
        ViewHolder holder = new ViewHolder();
        holder.cateName=convertView.findViewById(R.id.cateName);
        holder.prefix=convertView.findViewById(R.id.tv_prefix);
        holder.edit=convertView.findViewById(R.id.edit);
        holder.cancel=convertView.findViewById(R.id.cancel);
        holder.cateName.setText(cate.getName());
        holder.prefix.setText(cate.getPrefix());
        if(position==categories.size()-1)
        {
            holder.edit.setVisibility(View.INVISIBLE);
            holder.cancel.setVisibility(View.INVISIBLE);
        }else
        {
            holder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Edit_Category_Dialog custom_dialog = new Edit_Category_Dialog(categories,cate);
                    custom_dialog.show(fm, "Create category");
                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MessageDialog.getInstance(context,"Are you sure?","Are you want to delete this category ?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.service.deleteCategory(cate.getPrefix()).enqueue(new Callback<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code()==409)
                                    {
                                        MessageDialog.getInstance(context,"Error","Asset is available in category!").show();

                                    }
                                    if(response.code()==200)
                                    {
                                        List<Category> temp=categories.stream().filter(x->!x.getPrefix().equals(cate.getPrefix())).collect(Collectors.toList());
                                        categories.clear();
                                        categories.addAll(temp);
                                        notifyDataSetChanged();
                                        MessageDialog.getInstance(context,"Success","Delete category success").show();
                                        CreateNewAssetActivity.cateSelect=new Category();
                                        EditAssetActivity.cateSelect=new Category();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                        }
                    }).setNegativeButton("NO",null).show();
                }
            });
        }
        convertView.setTag(holder);
        return convertView;
    }
}
