package com.githiomi.onlineshoppingassistant.Ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
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

//    Local variables
    // The view pager adapter
    private DetailViewPagerAdapter detailViewPagerAdapter;
    // Parceled products
    private List<Product> parceledProducts;
    // The item position
    private int itemPosition;

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

        Log.d(TAG, "onCreate: item position " + itemPosition);

        if ( parceledProducts.size() > 0 && itemPosition > 0 ){
            Log.d(TAG, "onCreate: products parceled -------- " + parceledProducts );
        }

        initDetailViewPager();
    }

    // To initialize the view pager
    public void initDetailViewPager() {

        Log.d(TAG, "initProductAdapter: Product view pager init");
        detailViewPagerAdapter = new DetailViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, parceledProducts);

        wDetailViewPager.setAdapter(detailViewPagerAdapter);
        wDetailViewPager.setCurrentItem(itemPosition);

    }
}