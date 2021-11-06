package com.example.assets.Model;

public class CategorySelect {
    private String name;
    private String prefix;
    private int number;
    private int max;

    public CategorySelect() {
    }

    public CategorySelect(Category category, int number, int max) {
        this.name = category.getName();
        this.prefix = category.getPrefix();
        this.number = number;
        this.max = max;
    }

    public CategorySelect(String name, String prefix, int number, int max) {
        this.name = name;
        this.prefix = prefix;
        this.number = number;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
