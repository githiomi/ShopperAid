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

public class JumiaFragment extends Fragment {

    //    TAG
    private static final String TAG = JumiaFragment.class.getSimpleName();

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
    private List<Product> jumiaProducts;
    // The activity
    private Activity activity;

    //      Widgets
    @BindView(R.id.resultsRecyclerView) RecyclerView wJumiaRecyclerView;
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
    @BindView(R.id.noResult) TextView wNoResult;

    public JumiaFragment() {
        // Required empty public constructor
    }

    public static JumiaFragment newInstance() {
        return new JumiaFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View jumiaView = inflater.inflate(R.layout.fragment_jumia, container, false);

        // binding widgets
        ButterKnife.bind(this, jumiaView);

        // Get the context
        this.context = getContext();

        // Assigning activity
        this.activity = getActivity();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        // Init the web scrapping
        JumiaScraper jumiaScraper = new JumiaScraper();
        jumiaScraper.execute();

        return jumiaView;
    }

    public class JumiaScraper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: jumia scrape init");

            try {

                // Url to be used in browser
                String url = Constants.JUMIA_BASE_URL + productSearched;
                Document extractedContent = Jsoup.connect(url).get();

                // Confirming url
                Log.d(TAG, "doInBackground: extracted content url " + url);

                Elements dataObtained = extractedContent.select("a.core");

                if (dataObtained.size() > 0) {

                    int dataSize = dataObtained.size();

                    // initializing the list
                    jumiaProducts = new ArrayList<>();

                    for (int j = 0; j < dataSize; j += 1) {

                        if (productSearched.equals("iphone") || productSearched.equals("Iphone") || productSearched.equals("iPhone")) {

                            String linkToPage = dataObtained.select("a.core")
                                    .eq(j)
                                    .attr("href");

                            Log.d(TAG, "doInBackground: jumiaProductLink " + linkToPage);


                            String nameFromUrl = dataObtained.select("div.info")
                                    .select("h3.name")
                                    .eq(j)
                                    .text();

                            String imageFromUrl = dataObtained.select("div.imgClass")
                                    .select("img.src")
                                    .eq(j)
                                    .attr("data-src");

                            String priceFromUrl = dataObtained.select("div.sd")
                                    .select("div.prc")
                                    .eq(j)
                                    .text();

                            String ratingFromUrl = dataObtained.select("div.rev")
                                    .eq(j)
                                    .text();

                            jumiaProducts.add(new Product(linkToPage, nameFromUrl, priceFromUrl, ratingFromUrl, imageFromUrl));

                        } else {

                            String linkToPage = dataObtained.select("a.core")
                                    .eq(j)
                                    .attr("href");

                            String nameFromUrl = dataObtained.select("div.info")
                                    .select("h3.name")
                                    .eq(j)
                                    .text();

                            String imageFromUrl = dataObtained.select("div.img-c")
                                    .select("img.img")
                                    .eq(j)
                                    .attr("data-src");

                            String priceFromUrl = dataObtained.select("div.info")
                                    .select("div.prc")
                                    .eq(j)
                                    .text();

                            String ratingFromUrl = dataObtained.select("div.rev")
                                    .eq(j)
                                    .text();

                            if ( !(linkToPage.isEmpty()) || !(nameFromUrl.isEmpty()) || !(imageFromUrl.isEmpty()) || !(priceFromUrl.isEmpty()) || !(ratingFromUrl.isEmpty()) ) {
                                jumiaProducts.add(new Product(linkToPage, nameFromUrl, priceFromUrl, ratingFromUrl, imageFromUrl));
                            }

                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Passing the products to the adapter
                            passToAdapter(jumiaProducts);

                            // Method to hide progress bar
                            showResults();
                        }
                    });

                } else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noResult();
                        }
                    });
                }

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
        wJumiaRecyclerView.setVisibility(View.VISIBLE);
        wJumiaRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Jumia", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wJumiaRecyclerView.setAdapter(resultItemAdapter);
        wJumiaRecyclerView.setLayoutManager(gridLayoutManager);
        wJumiaRecyclerView.setClipToPadding(false);

    }
}