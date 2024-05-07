package com.oasis.todo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText username, password, confirmPassword;
    private Button signup;
    private TextView login;
    private DatabaseProvider DB;
    private LocalProvider localProvider;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        login = findViewById(R.id.textView5);
        signup = findViewById(R.id.signup);
        utils = new Utils();

        String text = "Already have an account? Login";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), text.indexOf("Login"), text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        login.setText(spannableString);

        DB = new DatabaseProvider(this);
        localProvider = new LocalProvider(this);

        signup.setOnClickListener(signUp());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private View.OnClickListener signUp() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String confirmPass = confirmPassword.getText().toString();

                if (!pass.equals(confirmPass)) {
                    utils.showDialog(SignUpActivity.this, "Error", "Passwords do not match", null);
                    return;
                }

                if (user.isEmpty() || pass.isEmpty()) {
                    utils.showDialog(SignUpActivity.this, "Error", "Please enter all the fields", null);
                } else {
                    boolean checkuser = DB.checkUsername(user);
                    if (!checkuser) {
                        boolean insert = DB.insertUser(user, pass);
                        if (insert) {
                            localProvider.setLoggedIn(true);
                            utils.showDialog(SignUpActivity.this, "Success", "Registration Successful", new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        } else {
                            Log.d("SignUpActivity", "Registration failed");
                            utils.showDialog(SignUpActivity.this, "Error", "Registration failed", null);
                        }
                    } else {
                        Log.d("SignUpActivity", "User already exists");
                        utils.showDialog(SignUpActivity.this, "Error", "User already exists", null);
                    }
                }
            }
        };
    }
}