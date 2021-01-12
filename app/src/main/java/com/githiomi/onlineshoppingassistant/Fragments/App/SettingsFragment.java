package com.githiomi.onlineshoppingassistant.Fragments.App;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.MainActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    //    TAG
    private static final String TAG = SettingsFragment.class.getSimpleName();

    //    Widgets
    @BindView(R.id.appInfo) CardView wAppInfoCv;
    @BindView(R.id.appTheme) CardView wAppTheme;
    @BindView(R.id.tvCurrentTheme) TextView wTvCurrentTheme;
    @BindView(R.id.switchAppTheme) SwitchMaterial wSwitchAppTheme;
    @BindView(R.id.appVersion) CardView wAppVersion;

    // Shared preferences
    private SharedPreferences.Editor sharedPreferencesEditor;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Butter knife binding widgets
        ButterKnife.bind(this, settingsView);

        // Get the context
        Context context = getContext();

        // Init the shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferencesEditor = sharedPreferences.edit();

        // Setting the switch
        wSwitchAppTheme.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        // Adding on click listeners
        wAppInfoCv.setOnClickListener(this);
        wAppTheme.setOnClickListener(this);
        wSwitchAppTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ( isChecked ){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferencesEditor.putString(Constants.APP_THEME, "Dark Mode").apply();
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferencesEditor.putString(Constants.APP_THEME, "Light Mode").apply();
                }
                restartApp();
            }
        });
        wAppVersion.setOnClickListener(this);

        return settingsView;
    }

    //      To restart the app with the new theme
    private void restartApp() {

        Intent restartApp = new Intent(getActivity(), MainActivity.class);
        restartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Objects.requireNonNull(getActivity()).startActivity(restartApp, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        getActivity().finish();

    }

    //      Method to perform action when a view is clicked
    @Override
    public void onClick(View v) {

        if (v == wAppInfoCv) {
            String appInfo = " No application information yet! :( ";
            Snackbar.make(Objects.requireNonNull(getView()), appInfo, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAction("Action", null).show();
        }

        if (v == wAppTheme) {
            String currentTheme = "";
            if ( wSwitchAppTheme.isChecked() ){
                currentTheme = "Dark Mode";
            }else {
                currentTheme = "Light Mode";
            }
            String appTheme = "The app is in " + currentTheme ;
            Snackbar.make(Objects.requireNonNull(getView()), appTheme, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAction("Action", null).show();
        }

        if (v == wAppVersion) {
            String appInfo = " Application Version: 1.0.0 :) ";
            Snackbar.make(Objects.requireNonNull(getView()), appInfo, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAction("Action", null).show();
        }

    }
}