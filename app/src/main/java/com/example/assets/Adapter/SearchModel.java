package com.example.assets.Adapter;

import com.example.assets.Model.User;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchModel implements Searchable {
    private String mtitle;


    public SearchModel setTitle(String title) {
        this.mtitle = title;
        return this;
    }

    public SearchModel(String title) {
        this.mtitle = title;
    }

    @Override
    public String getTitle() {
        return mtitle;
    }
}
