package com.githiomi.onlineshoppingassistant.Fragments.Ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.githiomi.onlineshoppingassistant.Adapters.ResultItemAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EbayFragment extends Fragment {

    //    TAG
    private static final String TAG = EbayFragment.class.getSimpleName();

    //    Widgets
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
    @BindView(R.id.noResult) TextView wNoResult;
    @BindView(R.id.resultsRecyclerView) RecyclerView wEbayRecyclerView;

    //    Local variables
    // Adapter
    private ResultItemAdapter resultItemAdapter;
    // Context
    private Context context;
    // Shared preferences
    private SharedPreferences sharedPreferences;
    // For the search inputted
    private String productSearched;
    // For retrieved products
    private List<Product> ebayProducts;
    // For the activity
    private Activity activity;

    public EbayFragment() {
        // Required empty public constructor
    }

    public static EbayFragment newInstance() {
        return new EbayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View ebayView = inflater.inflate(R.layout.fragment_ebay, container, false);

        // Binding widgets
        ButterKnife.bind(this, ebayView);

        // Init the context
        this.context = getContext();

        //  Assigning activity
        this.activity = getActivity();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        // Init the web scrapping
        EbayScrape ebayScrape = new EbayScrape();
        ebayScrape.execute();

        return ebayView;
    }

    public class EbayScrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: ebay scrape init");

            try {

                // init array list
                ebayProducts = new ArrayList<>();

                // Loop to get data from 2 pages
                for (int pageNumber = 1; pageNumber < 3; pageNumber += 1) {

                    // Url to be used in browser
                    String url = Constants.PRE_EBAY_BASE_URL + productSearched + Constants.POST_EBAY_BASE_URL + Constants.EBAY_PAGE_NO + pageNumber;
                    Document extractedContent = Jsoup.connect(url).get();

                    // Confirming url
                    Log.d(TAG, "doInBackground: extracted ebay content url " + url);

                    // Code that scrapes ebay
                    Elements obtainedData = extractedContent.select("li.s-item");

                    if (obtainedData.size() > 0) {

                        int dataSize = obtainedData.size();

                        for (int e = 0; e < dataSize; e += 1) {

                            String productLink = obtainedData
                                    .select("a.s-item__link")
                                    .eq(e)
                                    .attr("href");

                            String productName = obtainedData
                                    .select("h3.s-item__title")
                                    .eq(e)
                                    .text();

                            String productImage = obtainedData
                                    .select("img.s-item__image-img")
                                    .eq(e)
                                    .attr("src");

                            String productPrice = obtainedData
                                    .select("span.s-item__price")
                                    .eq(e)
                                    .text();

                            String productRating = obtainedData
                                    .select("div.x-star-rating")
                                    .select("span.clipped")
                                    .eq(e)
                                    .text();

                            if (!(productPrice.contains("to"))) {
                                if (!(productLink.isEmpty()) || !(productName.isEmpty()) || !(productImage.isEmpty()) || !(productPrice.isEmpty()) || !(productRating.isEmpty())) {
                                    ebayProducts.add(new Product(productLink, productName, productPrice, productRating, productImage));
                                }
                            }
                        }
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ( !(wEbayRecyclerView.isFocusable()) ) {
                                    noResult();
                                }
                            }
                        });
                    }
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Passing the products to the adapter
                        passToAdapter(ebayProducts);

                        // Method to hide progress bar
                        showResults();

                    }
                });

            } catch (Exception e) {
                System.out.println(e.getMessage());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUnsuccessful();
                    }
                });
            }
            return null;
        }
    }

    private void showUnsuccessful() {

        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        wErrorMessage.setVisibility(View.VISIBLE);
        wErrorMessage.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        Snackbar.make(Objects.requireNonNull(getView()), "Swipe Down To Refresh!", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                .setTextColor(getResources().getColor(R.color.white))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction("Action", null).show();

    }

    private void noResult() {

        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        wNoResult.setVisibility(View.VISIBLE);
        wNoResult.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    private void showResults() {

        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        wEbayRecyclerView.setVisibility(View.VISIBLE);
        wEbayRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Ebay", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wEbayRecyclerView.setAdapter(resultItemAdapter);
        wEbayRecyclerView.setLayoutManager(gridLayoutManager);
        wEbayRecyclerView.setClipToPadding(false);

    }
}