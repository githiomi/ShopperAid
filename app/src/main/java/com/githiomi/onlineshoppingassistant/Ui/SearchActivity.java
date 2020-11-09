package com.githiomi.onlineshoppingassistant.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, NavigationView.OnNavigationItemSelectedListener {

    //    TAG
    private static final String TAG = SearchActivity.class.getSimpleName();

    //    Widgets
    @BindView(R.id.search_drawer_layout)
    DrawerLayout wSearchDrawerLayout;
    @BindView(R.id.sideNavigation)
    NavigationView wSideNavigation;
    @BindView(R.id.edSearchInput)
    TextInputEditText wSearchInput;
    @BindView(R.id.btnSearch)
    Button wSearchButton;

    //    Local variables
    String productSearched;

    //    Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Binding widgets
        ButterKnife.bind(this);

        // Click listeners
        wSearchButton.setOnClickListener(this);
        // Navigation listeners
        wSideNavigation.setNavigationItemSelectedListener(this);

        // Setting up the navigation drawer
        wSideNavigation.bringToFront();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, wSearchDrawerLayout, R.string.open_navigation_drawer, R.string.close_navigation_drawer);
        wSearchDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // Setting initial selected item
        wSideNavigation.setCheckedItem(R.id.toSearchNav);

    }

    //    The method that will open the drawer layout
    public void clickMenu(View view) {
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
    }

    //    Methods to hide the keyboard
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == wSearchInput) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //    Method to perform the search
    private void performSearch(String searchText) {

        Toast.makeText(this, searchText, Toast.LENGTH_SHORT).show();

    }

    //    Methods for selection of navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.toSearchNav:
                // Do nothing
                break;

            case R.id.toProfileNav:
                Toast.makeText(this, "To Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.toLogoutNav:
                logout();
                break;

        }

        wSearchDrawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    //    Method that will log out the user
    private void logout() {

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
}