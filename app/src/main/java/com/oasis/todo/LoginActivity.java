package com.oasis.todo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.oasis.todo.provider.DatabaseProvider;
import com.oasis.todo.provider.LocalProvider;
import com.oasis.todo.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button login;
    private DatabaseProvider DB;
    private LocalProvider localProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        DB = new DatabaseProvider(this);
        localProvider = new LocalProvider(this);
        Utils utils = new Utils();


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);

        TextView textView = findViewById(R.id.textView5);

        String text = "Don't have an account? Sign Up";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf("Sign Up"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(user.isEmpty() || pass.isEmpty()) {
                    utils.showDialog(LoginActivity.this, "Error", "Please enter all the fields", null);
                }
                else{
                    boolean checkuserpass = DB.checkUsernamePassword(user, pass);
                    if (!checkuserpass) {
                        utils.showDialog(LoginActivity.this, "Error", "Invalid username or password", null);
                    } else {
                        localProvider.setLoggedIn(true);
                        utils.showDialog(LoginActivity.this, "Success", "Login Successful", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }
        });
    }
}