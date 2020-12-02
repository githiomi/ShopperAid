package com.githiomi.onlineshoppingassistant.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.githiomi.onlineshoppingassistant.Fragments.Ui.AmazonDetailFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.DetailFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.EbayDetailFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.JumiaDetailFragment;
import com.githiomi.onlineshoppingassistant.Models.Product;

import java.util.List;

public class DetailViewPagerAdapter extends FragmentPagerAdapter {

//    Local variables
    // The products
    private List<Product> parceledProducts;
    // Fragment name
    private String fragmentName;

    public DetailViewPagerAdapter(@NonNull FragmentManager fm, int behaviour, List<Product> products, String theFragment ) {
        super(fm, behaviour);

        this.parceledProducts = products;
        this.fragmentName = theFragment;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();

        if ( fragmentName.equals("Jumia") ) {
            Product productDetail = parceledProducts.get(position);
            fragment = JumiaDetailFragment.newInstance(productDetail);
        }

        if ( fragmentName.equals("Jiji") ) {
            Product productDetail = parceledProducts.get(position);
            fragment = DetailFragment.newInstance(productDetail);
        }

        if ( fragmentName.equals("Amazon") ) {
            Product productDetail = parceledProducts.get(position);
            fragment = AmazonDetailFragment.newInstance(productDetail);
        }

        if ( fragmentName.equals("Ebay") ) {
            Product productDetail = parceledProducts.get(position);
            fragment = EbayDetailFragment.newInstance(productDetail);
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return parceledProducts.size();
    }
}