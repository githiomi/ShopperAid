package com.githiomi.onlineshoppingassistant.Fragments.App;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    //    TAG
    private static final String TAG = SettingsFragment.class.getSimpleName();

    //    Widgets
    @BindView(R.id.appInfo) CardView wAppInfoCv;

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

        return settingsView;
    }

    //    Method init to replace the fragment with another view
    private void replaceView(Fragment fragment) {

        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.appFrameLayout, fragment);
        ft.commit();

    }

    //      Method to perform action when a view is clicked
    @Override
    public void onClick(View v) {

        if (v == wAppInfoCv) {
            String appInfo = " No app info yet! :( ";
            Snackbar.make(getView(), appInfo, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary)).setAction("Action", null).show();
        }
    }
}