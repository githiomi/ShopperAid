package com.githiomi.onlineshoppingassistant.Fragments.Ui;

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

public class KilimallFragment extends Fragment {

//    TAG
    private static final String TAG = KilimallFragment.class.getSimpleName();

//    Widgets
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
    @BindView(R.id.resultsRecyclerView) RecyclerView wKilimallRecyclerView;

//    Local variable
    // Adapter
    private ResultItemAdapter resultItemAdapter;
    // Context
    private Context context;
    // Search input
    private String productSearched;
    // Shared preferences
    private SharedPreferences sharedPreferences;
    // Retrieved products
    private List<Product> kilimallProducts;

    public KilimallFragment() {
        // Required empty public constructor
    }

    public static KilimallFragment newInstance() {
        return new KilimallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View kilimallView = inflater.inflate(R.layout.fragment_kilimall, container, false);

        // Binding widgets
        ButterKnife.bind(this, kilimallView);

        // Setting context
        this.context = getContext();

        // Get the product searched
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        Log.d(TAG, "onCreateView: product " + productSearched);

        // Init the web scrapping
        KilimallScrape kilimallScrape = new KilimallScrape();
        kilimallScrape.execute();

        return kilimallView;
    }

    public class KilimallScrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: kilimall scrape init");

            try {

                // Url to be used in browser
                String url = Constants.KILIMALL_BASE_URL + productSearched.trim();

                Document extractedContent = Jsoup.connect(url).get();
                Log.d(TAG, "doInBackground: Kilimall url " + url );

                // Confirming url
                Log.d(TAG, "doInBackground: extracted Kilimall content url " + url);

                // Code that scrapes ebay
                Elements obtainedData = extractedContent.select("li.s-item");

                int dataSize = obtainedData.size();

                if (dataSize > 0) {

                    kilimallProducts = new ArrayList<>();

                    for (int k = 0; k < dataSize; k += 1) {

                        String productName = obtainedData
                                .select("h3.s-item__title")
                                .eq(k)
                                .text();

                        String productImage = obtainedData
                                .select("img.s-item__image-img")
                                .eq(k)
                                .attr("src");

                        String productPrice = obtainedData
                                .select("span.s-item__price")
                                .eq(k)
                                .text();

                        String productRating = obtainedData
                                .select("div.b-starrating")
                                .eq(k)
                                .text();


                        kilimallProducts.add(new Product(" ", productName, productPrice, productRating, productImage));

                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUnsuccessful();
                        }
                    });
                }

                Log.d(TAG, "doInBackground: ------------------------------------------ " + kilimallProducts);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Passing the products to the adapter
                        passToAdapter(kilimallProducts);

                        // Method to hide progress bar
                        showResults();

                    }
                });
            } catch (Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wErrorMessage.setVisibility(View.VISIBLE);
                        wErrorMessage.setText("Couldn't retrieve data");
                        wProgressBar.setVisibility(View.GONE);
                        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
                    }
                });
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

        wKilimallRecyclerView.setVisibility(View.VISIBLE);
        wProgressBar.setVisibility(View.GONE);
        wProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        Log.d(TAG, "passToAdapter: Passed to adapter");

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wKilimallRecyclerView.setAdapter(resultItemAdapter);
        wKilimallRecyclerView.setLayoutManager(gridLayoutManager);
        wKilimallRecyclerView.setClipToPadding(false);

    }
}