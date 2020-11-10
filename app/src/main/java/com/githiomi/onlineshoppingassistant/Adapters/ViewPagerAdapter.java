package com.githiomi.onlineshoppingassistant.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.githiomi.onlineshoppingassistant.Fragments.JijiFragment;
import com.githiomi.onlineshoppingassistant.Fragments.JumiaFragment;
import com.githiomi.onlineshoppingassistant.Fragments.KilimallFragment;
import com.githiomi.onlineshoppingassistant.Fragments.WishFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    //    Local variables
    private String[] siteOptions;

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
            fragment = JumiaFragment.newInstance();
        }

        if (siteOption.equals("Kilimall")) {
            fragment = KilimallFragment.newInstance();
        }

        if (siteOption.equals("Jiji")) {
            fragment = JijiFragment.newInstance();
        }

        if (siteOption.equals("Wish")) {
            fragment = WishFragment.newInstance();
        }

        return fragment;
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
