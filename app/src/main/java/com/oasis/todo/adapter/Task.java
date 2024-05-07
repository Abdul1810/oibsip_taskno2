package com.oasis.todo.adapter;

public class Task {
    public String title;
    public boolean isChecked;

    public Task(String title, boolean isChecked) {
        this.title = title;
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}