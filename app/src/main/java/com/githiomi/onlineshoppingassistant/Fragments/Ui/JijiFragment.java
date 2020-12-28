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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JijiFragment extends Fragment {

    //    TAG
    private static final String TAG = JijiFragment.class.getSimpleName();

    //    Widgets
    @BindView(R.id.progressBar)
    ProgressBar wProgressBar;
    @BindView(R.id.errorMessage)
    TextView wErrorMessage;
    @BindView(R.id.noResult)
    TextView wNoResult;
    @BindView(R.id.resultsRecyclerView)
    RecyclerView wJijiRecyclerView;

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
    private List<Product> jijiProducts;
    // For the activity
    private Activity activity;

    public JijiFragment() {
        // Required empty public constructor
    }

    public static JijiFragment newInstance() {
        return new JijiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View jijiView = inflater.inflate(R.layout.fragment_jiji, container, false);

        // Binding widgets
        ButterKnife.bind(this, jijiView);

        // Init the context
        this.context = getContext();

        //  Assigning activity
        this.activity = getActivity();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        // Init the web scrapping
        JijiScrape jijiScrape = new JijiScrape();
        jijiScrape.execute();

        return jijiView;
    }

    public class JijiScrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: jiji scrape init");

            try {

                // Url to be used in browser
                String url = Constants.JIJI_BASE_URL + productSearched;
                Document extractedContent = Jsoup.connect(url).get();

                // Confirming url
                Log.d(TAG, "doInBackground: extracted jiji content url " + url);
                Elements dataObtained = extractedContent.select("div.qa-advert-listing.advert-listing");

                if (dataObtained.size() > 0) {

                    // initializing the list
                    jijiProducts = new ArrayList<>();

                    for (int ji = 0; ji < 60; ji += 1) {

                        String linkToPage = dataObtained
                                .select("a.js-handle-click-ctr.b-list-advert")
                                .eq(ji)
                                .attr("href");

                        String nameFromUrl = dataObtained
                                .select("div.b-advert-title-inner.qa-advert-title.b-advert-title-inner--h3")
                                .eq(ji)
                                .text();

                        String imageFromUrl = dataObtained
                                .select("picture.h-flex-center.h-width-100p.h-height-100p.h-overflow-hidden")
                                .select("img")
                                .eq(ji)
                                .attr("src");

                        String priceFromUrl = dataObtained
                                .select("div.b-list-advert__price.qa-advert-price")
                                .eq(ji)
                                .text()
                                .trim();

                        if (!(linkToPage.equals("")) || !(nameFromUrl.equals("")) || !(imageFromUrl.equals("")) || !(priceFromUrl.equals(""))) {
                            jijiProducts.add(new Product(linkToPage, nameFromUrl, priceFromUrl, "No rating available", imageFromUrl));
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Passing the products to the adapter
                            passToAdapter(jijiProducts);

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

        Toast.makeText(context, "Swipe down to refresh", Toast.LENGTH_SHORT).show();
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
        wJijiRecyclerView.setVisibility(View.VISIBLE);
        wJijiRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Ebay", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wJijiRecyclerView.setAdapter(resultItemAdapter);
        wJijiRecyclerView.setLayoutManager(gridLayoutManager);
        wJijiRecyclerView.setClipToPadding(false);

    }
}