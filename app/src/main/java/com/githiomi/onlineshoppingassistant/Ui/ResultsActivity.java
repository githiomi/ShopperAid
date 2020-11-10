package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.githiomi.onlineshoppingassistant.Adapters.ViewPagerAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultsActivity extends AppCompatActivity {

//    TAG
    private static final String TAG = ResultsActivity.class.getSimpleName();

//    Widgets
    @BindView(R.id.search_drawer_layout) DrawerLayout wSearchDrawerLayout;
    @BindView(R.id.sideNavigation) NavigationView wSideNavigation;
    @BindView(R.id.tvProductSearched) TextView wProductSearched;
    @BindView(R.id.resultViewPager) ViewPager wViewPager;

//    Local variables
    // The shopping options
    private String[] shoppingSiteOptions = { " Jumia ", " Kilimall ", " Jiji ", " Wish "};

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

}