package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.githiomi.onlineshoppingassistant.Adapters.DetailViewPagerAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

//    TAG
    private static final String TAG = DetailActivity.class.getSimpleName();

//    Widget
    @BindView(R.id.detailViewPager) ViewPager wDetailViewPager;
    @BindView(R.id.detailsRefresh) SwipeRefreshLayout wDetailsRefresh;

//    Local variables
    // The view pager adapter
    private DetailViewPagerAdapter detailViewPagerAdapter;
    // Parceled products
    private List<Product> parceledProducts;
    // The item position
    private int itemPosition;
    // For the fragment
    private String theFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Binding widgets
        ButterKnife.bind(this);

        // Getting data parceled
        Intent fromResultsActivity = getIntent();
        parceledProducts = Parcels.unwrap(fromResultsActivity.getParcelableExtra(Constants.WRAP_PRODUCT));
        itemPosition = fromResultsActivity.getIntExtra( Constants.ITEM_POSITION, 0 );
        theFragment = fromResultsActivity.getStringExtra(Constants.FRAGMENT_SOURCE);

        wDetailsRefresh.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initDetailViewPager();

                wDetailsRefresh.setRefreshing(false);

            }
        } );

        initDetailViewPager();
    }

    // To initialize the view pager
    public void initDetailViewPager() {

        detailViewPagerAdapter = new DetailViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, parceledProducts, theFragment);

        wDetailViewPager.setAdapter(detailViewPagerAdapter);
        wDetailViewPager.setCurrentItem(itemPosition);

    }
}