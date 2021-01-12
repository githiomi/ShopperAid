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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.RecentSearch;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    //    TAG
    private static final String TAG = SearchActivity.class.getSimpleName();

    //    Widgets
    @BindView(R.id.search_drawer_layout) DrawerLayout wSearchDrawerLayout;
    @BindView(R.id.sideNavigation) NavigationView wSideNavigation;
    @BindView(R.id.edSearchInput) TextInputEditText wSearchInput;
    @BindView(R.id.btnSearch) Button wSearchButton;
    @BindView(R.id.adContainer) FrameLayout wAdContainer;

    //    Local variables
    String productSearched;
    private SharedPreferences.Editor editor;

    //    Ads
    // Ad view
    private AdView adView;

    //    To alter the username
    View navigationView;
    CircleImageView wNavigationImage;
    TextView wNavigationUsername;
    TextView wLogout;

    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //    Firebase database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Binding widgets
        ButterKnife.bind(this);

        // Init shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Navigation listeners
        wSideNavigation.setNavigationItemSelectedListener(this);

        // Customize the navigation
        navigationView = wSideNavigation.getHeaderView(0);
        wNavigationUsername = (TextView) navigationView.findViewById(R.id.navUserUsername);
        wNavigationImage = (CircleImageView) navigationView.findViewById(R.id.navUserProfilePicture);
        wLogout = (TextView) navigationView.findViewById(R.id.toLogoutNav);

        // Setting up the navigation drawer
        wSideNavigation.bringToFront();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, wSearchDrawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        wSearchDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // Setting initial selected item
        wSideNavigation.setCheckedItem(R.id.toSearchNav);

        // Click listeners
        wSearchButton.setOnClickListener(this);
        wNavigationImage.setOnClickListener(this);
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
                    Uri userUri = signedInUser.getPhotoUrl();

                    // Setting user data (Navigation Drawer)
                    if (userUri != null) {
                        Picasso.get()
                                .load(userUri)
                                .into(wNavigationImage);
                    } else {
                        Picasso.get()
                                .load(R.drawable.user_profile_picture)
                                .into(wNavigationImage);
                    }
                }
            }
        };

        // Init ads
        MobileAds.initialize(this);

        // Ad view
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        wAdContainer.addView(adView);
        loadBanner();

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

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
        hideKeyboard(view);
        wSearchDrawerLayout.openDrawer(GravityCompat.START);
    }

    //    Method to open user recent searches
    public void recentSearches(View view) {
        if ( FirebaseAuth.getInstance().getCurrentUser() != null ) {
            Intent toRecents = new Intent(this, RecentSearchesActivity.class);
            startActivity(toRecents, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }else {
            hideKeyboard(view);
            String noRecents = "No Recent Searches For Guest Users!";
            Snackbar.make(view, noRecents, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                    .setTextColor(getResources().getColor(R.color.white))
                    .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                    .setAction("Action", null).show();
        }
    }

    //    Method implementation for the on click function
    @Override
    public void onClick(View v) {

        if (v == wSearchButton) {

            hideKeyboard(v);

            productSearched = wSearchInput.getText().toString().trim();

            if (productSearched.isEmpty()) {
                wSearchInput.setError("This field must not be left blank");
                return;
            }else {

                if ( FirebaseAuth.getInstance().getCurrentUser() != null ) {

                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    assert user != null;
                    String userName = user.getDisplayName();

                    assert userName != null;
                    databaseReference = FirebaseDatabase.getInstance()
                            .getReference("Recent Searches")
                            .child(userName)
                            .child(productSearched);

                    RecentSearch recentSearch = new RecentSearch(productSearched);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // Check if item is in recents
                            if ( !(snapshot.exists()) ){
                                databaseReference.setValue(recentSearch);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

                performSearch(productSearched);

            }
        }

        if (v == wNavigationImage) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                String asGuest = "You're not yet logged in!";
                wSideNavigation.setCheckedItem(R.id.toSearchNav);
                Snackbar.make(v, asGuest, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("Action", null).show();
            }
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (v == wNavigationUsername) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                String asGuest = "You're not yet logged in!";
                wSideNavigation.setCheckedItem(R.id.toSearchNav);
                Snackbar.make(v, asGuest, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                        .setTextColor(getResources().getColor(R.color.white))
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("Action", null).show();
            }
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //    Method to perform the search
    private void performSearch(String searchText) {

        Intent toResultActivity = new Intent(this, ResultsActivity.class);
        toResultActivity.putExtra(Constants.SEARCH_INPUT_KEY, searchText);

        // Saving to shared preferences
        editor.putString(Constants.SEARCH_INPUT_KEY, searchText).commit();

        startActivity(toResultActivity, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

    }

    //    Methods for selection of navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int selectedId = item.getItemId();

        if (selectedId == R.id.toSearchNav) {
            // Do nothing
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        }

        if (selectedId == R.id.toProfileNav) {

            wSearchDrawerLayout.closeDrawer(GravityCompat.START);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Intent toProfile = new Intent(this, ProfileActivity.class);
                startActivity(toProfile, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                Intent backToLogin = new Intent(this, LoginActivity.class);
                backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(backToLogin, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                finish();
            }
        }

        if (selectedId == R.id.toLogoutNav) {
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
            logout();
        }

        if (selectedId == R.id.toSettingsNav) {
            Intent toSettingsIntent = new Intent(this, AppActivity.class);
            toSettingsIntent.putExtra(Constants.APP_FRAGMENT_NAME, "Settings");
            startActivity(toSettingsIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

        if (selectedId == R.id.toPrivacyPolicyNav) {
            Intent toPrivacyPolicyIntent  = new Intent(this, AppActivity.class);
            toPrivacyPolicyIntent.putExtra(Constants.APP_FRAGMENT_NAME, "Privacy Policy");
            startActivity(toPrivacyPolicyIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }

        if (selectedId == R.id.toRateThisApp) {
            wSideNavigation.setCheckedItem(R.id.toSearchNav);
            Toast.makeText(this, "Link to app in play store", Toast.LENGTH_SHORT).show();
        }

        wSearchDrawerLayout.closeDrawer(GravityCompat.START);
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

    //    For the side navigation drawer
    @Override
    public void onBackPressed() {

        if (wSearchDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //    Method that will close the drawer on pause
    @Override
    protected void onPause() {
        super.onPause();
        if (wSearchDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Setting initial selected item
        wSideNavigation.setCheckedItem(R.id.toSearchNav);
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
}