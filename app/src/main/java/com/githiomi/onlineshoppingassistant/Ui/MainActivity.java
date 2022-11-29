package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme
        // Get the theme applied
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(Constants.APP_THEME, "Light Mode");

        if (theme.equals("Dark Mode")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

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
        }, 2000);
    }

    // Method that will take user to the login page.
    public void startApp(){

        Intent toLogin = new Intent(this, LoginActivity.class);
        toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(toLogin, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();

    }
}