package com.githiomi.onlineshoppingassistant.Ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.githiomi.onlineshoppingassistant.Adapters.ViewPagerAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Utils.ZoomOutPageTransformer;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    //    TAG
    private static final String TAG = ResultsActivity.class.getSimpleName();

    //    Widgets
    @BindView(R.id.search_drawer_layout) DrawerLayout wResultDrawerLayout;
    @BindView(R.id.sideNavigation) NavigationView wSideNavigation;
    @BindView(R.id.tvProductSearched) TextView wProductSearched;
    @BindView(R.id.resultViewPager) ViewPager wViewPager;
    @BindView(R.id.resultViewPagerStrip) PagerTabStrip wPagerTabStrip;
    @BindView(R.id.refreshResult) SwipeRefreshLayout wRefreshResults;
    @BindView(R.id.adContainer) FrameLayout wAdContainer;

    //    Local variables
    // The shopping options
    private final String[] shoppingSiteOptions = {"Jumia", "Amazon", "Ebay"};

    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //    To alter the username
    View navigationView;
    CircleImageView wNavigationProfilePicture;
    TextView wNavigationUsername;

    //    For the ads
    // Ad view
    private AdView adView;
    // Interstitial ad
    private InterstitialAd wInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Apply theme
        // Get the theme applied
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString(Constants.APP_THEME, "Light Mode");

        if (theme.equals("Dark Mode")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Binding widgets
        ButterKnife.bind(this);

        // Change color of the swipe progress
        wRefreshResults.setProgressBackgroundColorSchemeResource(R.color.colorPrimaryLight);

        // Init ads
        MobileAds.initialize(this);

        // Ads
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        wAdContainer.addView(adView);
        loadBanner();

        // Loading the interstitial add
        wInterstitialAd = new InterstitialAd(this);
        wInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //ca-app-pub-8763169533349627/1133294713
        AdRequest interstitialAdRequest = new AdRequest.Builder()
                .build();
        wInterstitialAd.loadAd(interstitialAdRequest);

        // Get search string
        String searchInput = getIntent().getStringExtra(Constants.SEARCH_INPUT_KEY);

        if (!(searchInput.isEmpty())) {
            wProductSearched.setText(searchInput);
        }

        // To refresh the results
        wRefreshResults.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                reInitViewPager(shoppingSiteOptions, wViewPager.getCurrentItem());

                wRefreshResults.setRefreshing(false);
            }
        });

        // Setting up the navigation drawer
        wSideNavigation.bringToFront();

        // Customize the navigation
        navigationView = wSideNavigation.getHeaderView(0);
        wNavigationProfilePicture = (CircleImageView) navigationView.findViewById(R.id.navUserProfilePicture);
        wNavigationUsername = (TextView) navigationView.findViewById(R.id.navUserUsername);

        // CLick listeners
        wNavigationProfilePicture.setOnClickListener(this);
        wNavigationUsername.setOnClickListener(this);

        // Initializing the firebase variables
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser signedInUser = firebaseAuth.getCurrentUser();

                if (signedInUser != null) {

                    // Getting user data from firebase
                    wNavigationUsername.setText(signedInUser.getDisplayName());
                    Uri uri = signedInUser.getPhotoUrl();

                    if (uri != null) {
                        Picasso.get().load(uri)
                                .into(wNavigationProfilePicture);
                    } else {
                        Picasso.get().load(R.drawable.user_profile_picture)
                                .into(wNavigationProfilePicture);
                    }

                } else {
                    Picasso.get()
                            .load(R.drawable.user_profile_picture)
                            .into(wNavigationProfilePicture);
                    wNavigationUsername.setText("Guest");
                }
            }
        };

        // Navigation listeners
        wSideNavigation.setNavigationItemSelectedListener(this);

        initViewPager(shoppingSiteOptions);

    }

    // For the adaptive banner
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

    //    Method to allow user to search another item
    public void optSearch(View view) {

        onBackPressed();

    }

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
        wResultDrawerLayout.openDrawer(GravityCompat.START);
    }

    //    Method to set up the view pager
    private void initViewPager(String[] shoppingSites) {

        // The view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), shoppingSites);

        // Slide animation
        wViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        wViewPager.setAdapter(viewPagerAdapter);
        wPagerTabStrip.setTextColor(getResources().getColor(R.color.black));
        wViewPager.setCurrentItem(0);

    }

    //    Method to refresh view pager
    private void reInitViewPager(String[] shoppingSites, int current) {

        // The view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), shoppingSites);

        // Slide animation
        wViewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        wViewPager.setAdapter(viewPagerAdapter);
        wPagerTabStrip.setTextColor(getResources().getColor(R.color.black));
        wViewPager.setCurrentItem(current);

    }

    //    Methods for selection of navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int selectedId = item.getItemId();

        if (selectedId == R.id.toSearchNav) {
            // Do nothing
            wResultDrawerLayout.closeDrawer(GravityCompat.START);
            Intent backToSearch = new Intent(this, SearchActivity.class);
            backToSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            onBackPressed();
        }

        if (selectedId == R.id.toProfileNav) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile);
            } else {
                Intent backToLogin = new Intent(this, LoginActivity.class);
                backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToLogin);
                finish();
            }
        }

        if (selectedId == R.id.toLogoutNav) {
            wResultDrawerLayout.closeDrawer(GravityCompat.START);
            logout();
        }

        if (selectedId == R.id.toSettingsNav) {
            Intent toSettingsIntent = new Intent(this, AppActivity.class);
            toSettingsIntent.putExtra(Constants.APP_FRAGMENT_NAME, "Settings");
            startActivity(toSettingsIntent);
        }

        if (selectedId == R.id.toPrivacyPolicyNav) {
            Intent toPrivacyPolicyIntent  = new Intent(this, AppActivity.class);
            toPrivacyPolicyIntent.putExtra(Constants.APP_FRAGMENT_NAME, "Privacy Policy");
            startActivity(toPrivacyPolicyIntent);
        }

        if (selectedId == R.id.toRateThisApp) {
            Toast.makeText(this, "Link to app in play store", Toast.LENGTH_SHORT).show();
        }

        wResultDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //    Method that will log out the user
    private void logout() {

        if (mFirebaseAuth.getCurrentUser() != null) {
            mFirebaseAuth.signOut();
        }
        Intent backToLogin = new Intent(this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();

    }

    //    On click methods
    @Override
    public void onClick(View v) {

        if (v == wNavigationProfilePicture) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile);
            } else {
                String asGuest = "You're not logged in!";
                Snackbar.make(v, asGuest, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("Action", null).show();
            }
            wResultDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (v == wNavigationUsername) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile);
            } else {
                String asGuest = "You're not logged in!";
                Snackbar.make(v, asGuest, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("Action", null).show();
            }
            wResultDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //    Firebase overriding listeners
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {

        if (wInterstitialAd.isLoaded()) {
            wInterstitialAd.show();

            wInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }
            });
        }
        deleteSharedPreferences(Constants.SEARCH_INPUT_KEY);
        super.onBackPressed();
    }
}