package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.githiomi.onlineshoppingassistant.Fragments.Profile.EditEmail;
import com.githiomi.onlineshoppingassistant.Fragments.Profile.EditUsername;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent currentIntent = getIntent();
        String fragmentDeterminant = currentIntent.getStringExtra(Constants.TO_EDIT);

        assert fragmentDeterminant != null;

        if ( fragmentDeterminant.equals(Constants.TO_EDIT_USERNAME) ){

            EditUsername editUsername = EditUsername.newInstance();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.editProfilerFrameLayout, editUsername);
            ft.commit();

        }
        else if (fragmentDeterminant.equals(Constants.TO_EDIT_EMAIL) ){

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