package com.oasis.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.oasis.todo.provider.DatabaseProvider;
import com.oasis.todo.provider.LocalProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Navigate to SignUpActivity immediately
        LocalProvider localProvider = new LocalProvider(this);
        DatabaseProvider databaseProvider = new DatabaseProvider(this);
        databaseProvider.onCreate(databaseProvider.getWritableDatabase());
        boolean isLoggedIn = localProvider.isLoggedIn();
        Log.d("MainActivity", "isLoggedIn: " + isLoggedIn);

        if (isLoggedIn) {
            // Navigate to HomeActivity if already logged in
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Navigate to SignUpActivity if not logged in
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}