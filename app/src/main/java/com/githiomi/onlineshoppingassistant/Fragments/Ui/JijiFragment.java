package com.githiomi.onlineshoppingassistant.Fragments.Ui;

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

public class JijiFragment extends Fragment {

    //    TAG
    private static final String TAG = JijiFragment.class.getSimpleName();

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
    private List<Product> jijiProducts;

    //      Widgets
    @BindView(R.id.resultsRecyclerView)
    RecyclerView wJumiaRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar wProgressBar;
    @BindView(R.id.errorMessage)
    TextView wErrorMessage;
    @BindView(R.id.noResult)
    TextView wNoResult;

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

        // binding widgets
        ButterKnife.bind(this, jijiView);

        // Get the context
        this.context = getContext();

        // Getting the search input
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        // Init the web scrapping
        JijiScraper jijiScraper = new JijiScraper();
        jijiScraper.execute();

        return jijiView;
    }

    public class JijiScraper extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: jiji scrape init");

            try {

                // Url to be used in browser
                String url = Constants.JIJI_BASE_URL + productSearched;
                Document extractedContent = Jsoup.connect(url).get();

                // Confirming url
                Log.d(TAG, "doInBackground: extracted jiji content url " + url);

                Elements dataObtained = extractedContent.select("div.b-list-advert__wrapper qa-advert-list-item");

                if (dataObtained.size() > 0) {

                    int dataSize = dataObtained.size();

                    // initializing the list
                    jijiProducts = new ArrayList<>();

                    for (int j = 0; j < dataSize; j += 1) {

                        String linkToPage = dataObtained
                                .select("a")
                                .eq(j)
                                .attr("href");

                        String nameFromUrl = dataObtained.select("div.b-advert-title-inner qa-advert-title b-advert-title-inner--h3")
                                .eq(j)
                                .text();

                        String imageFromUrl = dataObtained.select("span.b-list-advert__img")
                                .select("img")
                                .eq(j)
                                .attr("src");

                        String priceFromUrl = dataObtained.select("div.b-list-advert__price qa-advert-price")
                                .eq(j)
                                .text();

                        if (!(linkToPage.isEmpty()) || !(nameFromUrl.isEmpty()) || !(imageFromUrl.isEmpty()) || !(priceFromUrl.isEmpty()) ) {
                            jijiProducts.add(new Product(linkToPage.trim(), nameFromUrl.trim(), priceFromUrl.trim(), "No rating", imageFromUrl.trim()));
                        }

                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Passing the products to the adapter
                            passToAdapter(jijiProducts);

                            // Method to hide progress bar
                            showResults();
                        }
                    });

                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noResult();
                        }
                    });
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
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

        resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Jiji", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wJumiaRecyclerView.setAdapter(resultItemAdapter);
        wJumiaRecyclerView.setLayoutManager(gridLayoutManager);
        wJumiaRecyclerView.setClipToPadding(false);

    }
}