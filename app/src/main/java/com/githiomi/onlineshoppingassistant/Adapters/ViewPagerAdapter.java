package com.githiomi.onlineshoppingassistant.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.githiomi.onlineshoppingassistant.Fragments.Ui.EbayFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.JumiaFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.AmazonFragment;
import com.githiomi.onlineshoppingassistant.Fragments.Ui.AlibabaFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //    Local variables
    private final String[] siteOptions;

    public ViewPagerAdapter(@NonNull FragmentManager fm, String[] shoppingSiteOptions) {
        super(fm);

        this.siteOptions = shoppingSiteOptions;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();

        String siteOption = siteOptions[position];

        if (siteOption.equals("Jumia")) {
            return JumiaFragment.newInstance();
        }

        if (siteOption.equals("Ebay")){
            return EbayFragment.newInstance();
        }

        if (siteOption.equals("Alibaba")) {
            return AlibabaFragment.newInstance();
        }

        if (siteOption.equals("Amazon")) {
            return AmazonFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return siteOptions.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return siteOptions[position];
    }

}