package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.githiomi.onlineshoppingassistant.Fragments.App.PrivacyPolicyFragment;
import com.githiomi.onlineshoppingassistant.Fragments.App.SettingsFragment;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppActivity extends AppCompatActivity {

    //    TAG
    private static final String TAG = AppActivity.class.getSimpleName();

    //    Widgets
    @BindView(R.id.adContainer) FrameLayout wAdContainer;
    @BindView(R.id.tvAppFragment) TextView wTvAppFragment;

    //    Local variables
    // Ad view
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        // Butter knife binding views
        ButterKnife.bind(this);

        // Init the ads
        MobileAds.initialize(this);

        // Load the add
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        wAdContainer.addView(adView);
        loadBanner();

        // Get which fragment
        Intent fromNavigation = getIntent();
        String fragmentName = fromNavigation.getStringExtra(Constants.APP_FRAGMENT_NAME);

        assert fragmentName != null;

        if (fragmentName.equals("Settings")) {

            wTvAppFragment.setText(fragmentName);
            SettingsFragment settingsFragment = SettingsFragment.newInstance();
            replaceView(settingsFragment);

        }

        if (fragmentName.equals("Privacy Policy")) {

            wTvAppFragment.setText(fragmentName);
            PrivacyPolicyFragment privacyPolicyFragment = PrivacyPolicyFragment.newInstance();
            replaceView(privacyPolicyFragment);

        }
    }

    //    For the adaptive banner
    private void loadBanner() {
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    // Method that replaces the fragment view
    private void replaceView(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.appFrameLayout, fragment);
        fragmentTransaction.commit();

    }

    // When user clicks the back image view
    public void backFromApp (View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}