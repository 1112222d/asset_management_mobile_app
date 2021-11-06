package com.example.assets.Adapter;

import static com.example.assets.UserActivity.CreateNewAssignActivity.customListViewCategorySelectAdapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assets.Model.Category;
import com.example.assets.Model.CategorySelect;
import com.example.assets.R;

import java.util.List;

public class CustomListViewCategorySelectAdapter extends BaseAdapter {

    Context context;
    List<CategorySelect> categories;


    public CustomListViewCategorySelectAdapter(Context context, List<CategorySelect> categories) {
        this.context = context;
        this.categories = categories;

    }

    @Override
    public int getCount() {
        return categories.size();
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
        TextView prefix, name;
        EditText number;
        ImageView plus,minus,delete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_select_category, parent, false);
        CategorySelect category = (CategorySelect) getItem(position);
        ViewHolder holder;
        holder = new ViewHolder();
        holder.prefix = convertView.findViewById(R.id.prefix);
        holder.name = convertView.findViewById(R.id.name);
        holder.number = convertView.findViewById(R.id.number_Select);
        holder.plus=convertView.findViewById(R.id.plus);
        holder.minus=convertView.findViewById(R.id.minus);
        holder.delete=convertView.findViewById(R.id.delete);
        holder.prefix.setText(category.getPrefix());
        holder.name.setText(category.getName());
        holder.number.setText(String.valueOf(category.getNumber()));
        holder.minus.setOnClickListener(v->{
            if(category.getNumber()>1)
            {
                categories.remove(category);
                category.setNumber(category.getNumber()-1);
                categories.add(category);
                holder.number.setText(String.valueOf(category.getNumber()));
            }else
            {
                Toast.makeText(parent.getContext(), "The minimum amount is 1", Toast.LENGTH_SHORT).show();
            }
        });
        holder.plus.setOnClickListener(v->{
            if(category.getNumber()< category.getMax())
            {
                categories.remove(category);
                category.setNumber(category.getNumber()+1);
                categories.add(category);
                holder.number.setText(String.valueOf(category.getNumber()));
            }else
            {
                Toast.makeText(parent.getContext(), "The maximum amount is "+category.getMax(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b)
                {
                    if(Integer.parseInt(holder.number.getText().toString())> category.getMax())
                    {
                        categories.remove(category);
                        category.setNumber(category.getMax());
                        categories.add(category);
                        holder.number.setText(String.valueOf(category.getNumber()));
                        Toast.makeText(parent.getContext(), "The maximum amount is "+category.getMax(), Toast.LENGTH_SHORT).show();
                    }
                    if(Integer.parseInt(holder.number.getText().toString())<1)
                    {
                        categories.remove(category);
                        category.setNumber(1);
                        categories.add(category);
                        holder.number.setText(String.valueOf(category.getNumber()));
                        Toast.makeText(parent.getContext(), "The minimum amount is 1", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categories.remove(category);
                customListViewCategorySelectAdapter.notifyDataSetChanged();
            }
        });
        convertView.setTag(holder);
        return convertView;
    }

}

