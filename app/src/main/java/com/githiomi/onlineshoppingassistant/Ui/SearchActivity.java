package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.githiomi.onlineshoppingassistant.Adapters.Firebase.RecentSearchesAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
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

    //    Local variables
    String productSearched;
    private SharedPreferences.Editor editor;

    //    To alter the username
    View navigationView;
    CircleImageView wNavigationImage;
    TextView wNavigationUsername;

    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    // Database
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Binding widgets
        ButterKnife.bind(this);
        //Adview
        AdView wAdView = findViewById(R.id.adView);

        MobileAds.initialize(this);

        // Loading adds
        AdRequest adRequest = new AdRequest.Builder().build();
        wAdView.loadAd(adRequest);

        // Init shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Init firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            databaseReference = FirebaseDatabase.getInstance().getReference("Recent Searches").child(currentUser);
        }

        // Navigation listeners
        wSideNavigation.setNavigationItemSelectedListener(this);

        // Customize the navigation
        navigationView = wSideNavigation.getHeaderView(0);
        wNavigationUsername = (TextView) navigationView.findViewById(R.id.navUserUsername);
        wNavigationImage = (CircleImageView) navigationView.findViewById(R.id.navUserProfilePicture);

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

                } else {
                    wNavigationUsername.setText("Guest");
                }
            }
        };
    }

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
        hideKeyboard(view);
        wSearchDrawerLayout.openDrawer(GravityCompat.START);
    }

    //    Method implementation for the on click function
    @Override
    public void onClick(View v) {

        if (v == wSearchButton) {

            hideKeyboard(v);

            productSearched = wSearchInput.getText().toString().trim();

            if (productSearched.isEmpty()) {
                wSearchInput.setError("This field cannot be left blank");
                return;
            }

            performSearch(productSearched);

        }

        if (v == wNavigationImage) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (v == wNavigationUsername) {
            startActivity(new Intent(this, ProfileActivity.class));
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
        editor.putString(Constants.SEARCH_INPUT_KEY, searchText).apply();

        // Saving text to firebase
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            databaseReference.child(searchText).setValue(searchText);
        }

        startActivity(toResultActivity);

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
                toProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
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

        FirebaseAuth.getInstance().signOut();
        Intent backToLogin = new Intent(this, LoginActivity.class);
        backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backToLogin);
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
}