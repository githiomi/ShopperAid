package com.githiomi.onlineshoppingassistant.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class EbayFragment extends Fragment {

//    TAG
    private static final String TAG = EbayFragment.class.getSimpleName();

//    Widgets
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
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
        View amazonView = inflater.inflate(R.layout.fragment_ebay, container, false);

        // Binding widgets
        ButterKnife.bind(this, amazonView);

        // Init the context
        this.context = getContext();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        Log.d(TAG, "onCreateView: product " + productSearched);

        // Init the web scrapping
        AmazonScrape amazonScrape = new AmazonScrape();
        amazonScrape.execute();

        return amazonView;
    }

    public class AmazonScrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: ebay scrape init");

            try {

                // Url to be used in browser
                String url = Constants.PRE_EBAY_BASE_URL + productSearched + Constants.POST_EBAY_BASE_URL;

                Document extractedContent = Jsoup.connect(url).get();
                Log.d(TAG, "doInBackground: eBay url " + url );

                // Confirming url
                Log.d(TAG, "doInBackground: extracted ebay content url " + url);

                // Code that scrapes ebay
                Elements obtainedData = extractedContent.select("li.s-item");

                int dataSize = obtainedData.size();

                if (dataSize > 0) {

                    ebayProducts = new ArrayList<>();

                    for (int a = 0; a < dataSize; a += 1) {

                        String productName = obtainedData
                                .select("h3.s-item__title")
                                .eq(a)
                                .text();

                        String productImage = obtainedData
                                .select("img.s-item__image-img")
                                .eq(a)
                                .attr("src");

                        String productPrice = obtainedData
                                .select("span.s-item__price")
                                .eq(a)
                                .text();

                        String productRating = obtainedData
                                .select("div.b-starrating")
                                .eq(a)
                                .text();


                        ebayProducts.add(new Product(" ", productName, productPrice, productRating, productImage));

                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUnsuccessful();
                        }
                    });
                }

                Log.d(TAG, "doInBackground: ------------------------------------------ " + ebayProducts);

                getActivity().runOnUiThread(new Runnable() {
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
            }
            return null;
        }
    }

    private void showUnsuccessful() {

        wErrorMessage.setVisibility(View.VISIBLE);
        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));

    }

    private void showResults() {

        wEbayRecyclerView.setVisibility(View.VISIBLE);
        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        Log.d(TAG, "passToAdapter: Passed to adapter");

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wEbayRecyclerView.setAdapter(resultItemAdapter);
        wEbayRecyclerView.setLayoutManager(gridLayoutManager);
        wEbayRecyclerView.setClipToPadding(false);

    }
}