package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.githiomi.onlineshoppingassistant.Adapters.ViewPagerAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
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
    @BindView(R.id.search_drawer_layout) DrawerLayout wSearchDrawerLayout;
    @BindView(R.id.sideNavigation) NavigationView wSideNavigation;
    @BindView(R.id.tvProductSearched) TextView wProductSearched;
    @BindView(R.id.resultViewPager) ViewPager wViewPager;
    @BindView(R.id.refreshResult) SwipeRefreshLayout wRefreshResults;

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

    //    Interstitial ad
    private InterstitialAd wInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Binding widgets
        ButterKnife.bind(this);
        //Adview
        AdView wAdView = findViewById(R.id.adView);
        MobileAds.initialize(this);

        // Loading adds
        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        wAdView.loadAd(bannerAdRequest);

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

    //    Method to allow user to search another item
    public void optSearch(View view) {

        onBackPressed();

    }

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
        wSearchDrawerLayout.openDrawer(GravityCompat.START);
    }

    //    Method to set up the view pager
    private void initViewPager(String[] shoppingSites) {

        // The view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), shoppingSites);

        wViewPager.setAdapter(viewPagerAdapter);
        wViewPager.setCurrentItem(0);

    }

    //    Method to refresh view pager
    private void reInitViewPager(String[] shoppingSites, int current) {

        // The view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), shoppingSites);

        wViewPager.setAdapter(viewPagerAdapter);
        wViewPager.setCurrentItem(current);

    }

    //    Methods for selection of navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int selectedId = item.getItemId();

        if (selectedId == R.id.toSearchNav) {
            // Do nothing
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
            Intent backToSearch = new Intent(this, SearchActivity.class);
            backToSearch.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            onBackPressed();
        }

        if (selectedId == R.id.toProfileNav) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile);
            } else {
                String asGuest = "You're not logged in";
                wSideNavigation.setCheckedItem(R.id.toSearchNav);
                Toast.makeText(this, asGuest, Toast.LENGTH_SHORT).show();
            }
        }

        if (selectedId == R.id.toLogoutNav) {
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
            logout();
        }

        return true;
    }

    //    Method that will log out the user
    private void logout() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
        Intent backToLogin = new Intent(this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin);
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
                String asGuest = "You're not logged in";
                wSideNavigation.setCheckedItem(R.id.toSearchNav);
                Toast.makeText(this, asGuest, Toast.LENGTH_SHORT).show();
            }
        }

        if (v == wNavigationUsername) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile);
            } else {
                String asGuest = "You're not logged in";
                wSideNavigation.setCheckedItem(R.id.toSearchNav);
                Toast.makeText(this, asGuest, Toast.LENGTH_SHORT).show();
            }
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
        if (mFirebaseAuth != null) {
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
        super.onBackPressed();
    }
}