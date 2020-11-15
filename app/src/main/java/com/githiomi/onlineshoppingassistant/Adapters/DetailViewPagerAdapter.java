package com.githiomi.onlineshoppingassistant.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.githiomi.onlineshoppingassistant.Fragments.Ui.DetailFragment;
import com.githiomi.onlineshoppingassistant.Models.Product;

import java.util.List;

public class DetailViewPagerAdapter extends FragmentPagerAdapter {

//    Local variables
    // The products
    private List<Product> parceledProducts;

    public DetailViewPagerAdapter(@NonNull FragmentManager fm, int behaviour, List<Product> products ) {
        super(fm, behaviour);

        this.parceledProducts = products;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Product productDetail = parceledProducts.get(position);
        return DetailFragment.newInstance( productDetail );
    }

    @Override
    public int getCount() {
        return parceledProducts.size();
    }
}
