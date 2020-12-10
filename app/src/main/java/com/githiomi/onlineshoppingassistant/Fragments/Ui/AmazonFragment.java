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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.githiomi.onlineshoppingassistant.Adapters.ResultItemAdapter;
import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmazonFragment extends Fragment {

    //    TAG
    private static final String TAG = AmazonFragment.class.getSimpleName();

    //    Local variables
    // Results adapter
    private ResultItemAdapter resultItemAdapter;
    // Context
    private Context context;
    // Shared preferences
    private SharedPreferences sharedPreferences;
    // The product searched
    private String productSearched;
    // Results list
    private List<Product> amazonProducts;

    //      Widgets
    @BindView(R.id.resultsRecyclerView) RecyclerView wAmazonRecyclerView;
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
    @BindView(R.id.noResult) TextView wNoResult;

    public AmazonFragment() {
        // Required empty public constructor
    }

    public static AmazonFragment newInstance() {
        return new AmazonFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View amazonView = inflater.inflate(R.layout.fragment_amazon, container, false);

        // Binding widgets
        ButterKnife.bind(this, amazonView);

        // Context
        this.context = getContext();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        // Init the scrapper
        AmazonScraper amazonScraper = new AmazonScraper();
        amazonScraper.execute();

        return amazonView;

    }

    public class AmazonScraper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Activity thisActivity = getActivity();
            Log.d(TAG, "doInBackground: amazon scrape init");

            try {

                // Url to be used in browser
                String url = Constants.PRE_AMAZON_BASE_URL + productSearched.trim() + Constants.POST_AMAZON_BASE_URL;
                Document extractedContent = Jsoup.connect(url).get();

                // Confirming url
                Log.d(TAG, "doInBackground: extracted amazon content url " + url);
                Elements dataObtained = extractedContent.select("div.sg-col-inner");

                if (dataObtained.size() > 0) {

                    int dataSize = dataObtained.size();

                    // initializing the list
                    amazonProducts = new ArrayList<>();

                    for (int a = 0; a < dataSize; a += 1) {

                        String linkToPage = dataObtained
                                .select("a.a-link-normal.s-no-outline")
                                .eq(a)
                                .attr("href");

                        Log.d(TAG, "doInBackground: amazonAsin: " + linkToPage);

                        String nameFromUrl = dataObtained
                                .select("span.a-size-base-plus.a-color-base.a-text-normal")
                                .eq(a)
                                .text();

                        String imageFromUrl = dataObtained
                                .select("img.s-image")
                                .eq(a)
                                .attr("src");

                        String priceFromUrl = dataObtained
                                .select("div.a-row")
                                .select("span.a-offscreen")
                                .eq(a)
                                .text();

                        String ratingFromUrl = dataObtained
                                .select("span.a-icon-alt")
                                .eq(a)
                                .text();

                        if ( !(linkToPage.equals("")) || !(nameFromUrl.equals("")) || !(imageFromUrl.equals("")) || !(priceFromUrl.equals("")) || !(ratingFromUrl.equals(""))) {
                            amazonProducts.add(new Product(linkToPage, nameFromUrl, priceFromUrl, ratingFromUrl, imageFromUrl));
                        }
                    }

                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Passing the products to the adapter
                            passToAdapter(amazonProducts);

                            // Method to hide progress bar
                            showResults();
                        }
                    });

                } else {
                    thisActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noResult();
                        }
                    });
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                thisActivity.runOnUiThread(new Runnable() {
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
        wAmazonRecyclerView.setVisibility(View.VISIBLE);
        wAmazonRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Amazon", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wAmazonRecyclerView.setAdapter(resultItemAdapter);
        wAmazonRecyclerView.setLayoutManager(gridLayoutManager);
        wAmazonRecyclerView.setClipToPadding(false);

    }
}