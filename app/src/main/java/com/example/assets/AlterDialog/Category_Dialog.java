package com.example.assets.AlterDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.assets.AdminActivity.CreateNewAssetActivity;
import com.example.assets.MainActivity;
import com.example.assets.Model.Category;
import com.example.assets.Model.User;
import com.example.assets.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Category_Dialog extends AppCompatDialogFragment {
    TextInputEditText name,prefix;
    List<Category> categories;

    public Category_Dialog(List<Category> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog_create_cate, null);

        name = view.findViewById(R.id.nameCreateCate);
        prefix=view.findViewById(R.id.prefixCreateCate);
        builder.setView(view).setTitle("Create category:").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        return builder.create();
    }
    private void loadCate(){
        MainActivity.service.getCategory().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.code()==200)
                {
                    categories.clear();
                    categories.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d= (AlertDialog)getDialog();
        if(d!=null) {
            Button button = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(prefix.getText().toString().length()!=2)
                    {
                        Toast.makeText(v.getContext(), "Prefix length must be 2", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        if(name.getText().toString().trim().length()==0)
                        {
                            Toast.makeText(v.getContext(), "Name must be bot blank", Toast.LENGTH_SHORT).show();

                        }else
                        {
                            Category category = new Category(name.getText().toString(),prefix.getText().toString());
                            MainActivity.service.createCategory(category).enqueue(new Callback<Category>() {
                                @Override
                                public void onResponse(Call<Category> call, Response<Category> response) {
                                    if(response.code()==200)
                                    {
                                        MessageDialog.getInstance(v.getContext(), "Success",
                                                "Create category success").show();
                                        dismiss();
                                        loadCate();
                                    }else
                                    {
                                        MessageDialog.getInstance(v.getContext(), "Error",
                                                "Something went wrong").show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Category> call, Throwable t) {
                                    MessageDialog.getInstance(v.getContext(), "Error",
                                            "Something went wrong").show();
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
