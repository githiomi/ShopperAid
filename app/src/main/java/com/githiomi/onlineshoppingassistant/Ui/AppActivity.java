package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.githiomi.onlineshoppingassistant.Fragments.App.PrivacyPolicyFragment;
import com.githiomi.onlineshoppingassistant.Fragments.App.SettingsFragment;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;

import butterknife.ButterKnife;

public class AppActivity extends AppCompatActivity {

    //    TAG
    private static final String TAG = AppActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        // Butter knife binding views
        ButterKnife.bind(this);

        // Get which fragment
        Intent fromNavigation = getIntent();
        String fragmentName = fromNavigation.getStringExtra(Constants.APP_FRAGMENT_NAME);

        assert fragmentName != null;

        if (fragmentName.equals("Settings")) {

            SettingsFragment settingsFragment = SettingsFragment.newInstance();
            replaceView(settingsFragment);

        }

        if (fragmentName.equals("Privacy Policy")) {

            PrivacyPolicyFragment privacyPolicyFragment = PrivacyPolicyFragment.newInstance();
            replaceView(privacyPolicyFragment);

        }
    }

    // Method that replaces the fragment view
    private void replaceView(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.appFrameLayout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}