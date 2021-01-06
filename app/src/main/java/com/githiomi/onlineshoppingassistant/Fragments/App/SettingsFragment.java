package com.githiomi.onlineshoppingassistant.Fragments.App;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    //    TAG
    private static final String TAG = SettingsFragment.class.getSimpleName();

    //    Widgets
    @BindView(R.id.appInfo) CardView wAppInfoCv;
    @BindView(R.id.appVersion) CardView wAppVersion;

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

        // Adding on click listeners
        wAppInfoCv.setOnClickListener(this);
        wAppVersion.setOnClickListener(this);

        return settingsView;
    }

    //      Method to perform action when a view is clicked
    @Override
    public void onClick(View v) {

        if (v == wAppInfoCv) {
            String appInfo = " No application information yet! :( ";
            Snackbar.make(Objects.requireNonNull(getView()), appInfo, Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setAction("Action", null).show();
        }

        if (v == wAppVersion) {
            String appInfo = " BaseApplication Version: 1.0 :) ";
            Snackbar.make(Objects.requireNonNull(getView()), appInfo, Snackbar.LENGTH_SHORT)
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setAction("Action", null).show();
        }

    }
}