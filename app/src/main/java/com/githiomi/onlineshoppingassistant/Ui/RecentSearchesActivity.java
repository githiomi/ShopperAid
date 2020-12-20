package com.githiomi.onlineshoppingassistant.Ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.githiomi.onlineshoppingassistant.Adapters.Firebase.RecentAdapter;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Utils.ItemSwipeHelperCallback;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecentSearchesActivity extends AppCompatActivity {

    //    TAG
    private static final String TAG = RecentSearchesActivity.class.getSimpleName();

    //    Widgets
    @BindView(R.id.recentSearchesRecyclerView) RecyclerView wRecentRecyclerView;
    @BindView(R.id.tvNoRecent) TextView wNoRecentText;
    @BindView(R.id.recentProgressBar) ProgressBar wRecentProgressBar;
    @BindView(R.id.adContainer) FrameLayout wAdContainer;

    //    Local variables
    // The adapter
    private RecentAdapter recentAdapter;
    // Firebase
    private DatabaseReference databaseReference;
    // Item touch helper
    private ItemTouchHelper itemTouchHelper;

    //      Ads
    // Ad view
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_searches);

        // Binding widgets
        ButterKnife.bind(this);

        // Init ads
        MobileAds.initialize(this);

        // Ads
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                            // ca-app-pub-8763169533349627/1914234789
        wAdContainer.addView(adView);
        loadBanner();

        // Getting user data
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userName = currentUser.getDisplayName();

        // Database reference
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Recent Searches")
                .child(userName);

        // Method to get data from firebase
        getRecentSearches();

    }

    // Method to get user data
    private void getRecentSearches() {

        Activity activity = this;

        // Creating reference
        FirebaseRecyclerOptions<String> recentOptions = new FirebaseRecyclerOptions.Builder<String>()
                .setQuery(databaseReference.orderByValue(), String.class)
                .build();

        // Checking if data exists
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    wRecentProgressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
                    wRecentProgressBar.setVisibility(View.GONE);
                    wRecentRecyclerView.setVisibility(View.VISIBLE);
                    wRecentRecyclerView.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));

                } else {

                    wRecentProgressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
                    wRecentProgressBar.setVisibility(View.GONE);
                    wNoRecentText.setVisibility(View.VISIBLE);
                    wNoRecentText.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled: ERROR!! ---- " + error);
                wRecentProgressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
                wRecentProgressBar.setVisibility(View.GONE);
                wNoRecentText.setVisibility(View.VISIBLE);
                wNoRecentText.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));

            }
        });

        recentAdapter = new RecentAdapter(recentOptions, databaseReference, this);

        // Setting adapter
        wRecentRecyclerView.setAdapter(recentAdapter);
        wRecentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Allow swipe to delete
        ItemTouchHelper.Callback callback = new ItemSwipeHelperCallback(recentAdapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(wRecentRecyclerView);

        wRecentRecyclerView.setHasFixedSize(true);
        recentAdapter.notifyDataSetChanged();
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

    // Method when back arrow is clicked
    public void backToSearch(View view) {
        onBackPressed();
    }

    // Overriding on start and stop methods
    @Override
    public void onStart() {
        super.onStart();

        recentAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (recentAdapter != null) {
            recentAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}