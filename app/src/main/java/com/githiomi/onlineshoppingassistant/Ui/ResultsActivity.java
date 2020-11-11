package com.githiomi.onlineshoppingassistant.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.githiomi.onlineshoppingassistant.Adapters.ViewPagerAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    TAG
    private static final String TAG = ResultsActivity.class.getSimpleName();

//    Widgets
    @BindView(R.id.search_drawer_layout) DrawerLayout wSearchDrawerLayout;
    @BindView(R.id.sideNavigation) NavigationView wSideNavigation;
    @BindView(R.id.tvProductSearched) TextView wProductSearched;
    @BindView(R.id.resultViewPager) ViewPager wViewPager;

//    Local variables
    // The shopping options
    private final String[] shoppingSiteOptions = { "Jumia", "Kilimall", "Ebay", "Wish" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Binding widgets
        ButterKnife.bind(this);

        // Get search string
        String searchInput = getIntent().getStringExtra(Constants.SEARCH_INPUT_KEY);

        if ( !(searchInput.isEmpty()) ){
            wProductSearched.setText(searchInput);
        }

        // Setting up the navigation drawer
        wSideNavigation.bringToFront();

        // Navigation listeners
        wSideNavigation.setNavigationItemSelectedListener(this);

        initViewPager(shoppingSiteOptions);

    }

//    The method that will open the drawer layout
    public void clickMenu(View view) {
        wSearchDrawerLayout.openDrawer(GravityCompat.START);
    }

//    Method to set up the view pager
    private void initViewPager( String[] shoppingSites ) {

        // The view pager adapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), shoppingSites);

        wViewPager.setAdapter(viewPagerAdapter);
        wViewPager.setCurrentItem(0);

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
            startActivity(backToSearch);
            finish();
        }

        if (selectedId == R.id.toProfileNav) {
            wSearchDrawerLayout.closeDrawer(GravityCompat.START);
            Toast.makeText(this, "To Profile", Toast.LENGTH_SHORT).show();
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

}