package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.githiomi.onlineshoppingassistant.Fragments.Profile.EditEmail;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;

public class EditActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_edit);

        Intent currentIntent = getIntent();
        String fragmentDeterminant = currentIntent.getStringExtra(Constants.TO_EDIT);

        assert fragmentDeterminant != null;

        if (fragmentDeterminant.equals(Constants.TO_EDIT_EMAIL) ){

            EditEmail editEmail = EditEmail.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.editProfilerFrameLayout, editEmail);
            ft.commit();

        }

    }

    // To confirm exit
    @Override
    public void onBackPressed() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("Are you sure you want to discard changes made to your profile?")
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }
}