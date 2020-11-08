package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.githiomi.onlineshoppingassistant.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();
    }

    // Method that will delay before tha app opens.
    public void load(){

        Handler progressHandler = new Handler();

        progressHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startApp();
            }
        }, 3000);
    }

    // Method that will take user to the login page.
    public void startApp(){

        Intent toLogin = new Intent(this, LoginActivity.class);
        toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toLogin);
        finish();

    }

}