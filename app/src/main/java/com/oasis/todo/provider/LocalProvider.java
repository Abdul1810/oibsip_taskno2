package com.oasis.todo.provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class LocalProvider {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public LocalProvider(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}