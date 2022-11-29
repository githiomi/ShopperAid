package com.githiomi.onlineshoppingassistant.Fragments.Ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlibabaFragment extends Fragment {

    //    TAG
    private static final String TAG = AlibabaFragment.class.getSimpleName();

    // Context
    private Context context;
    // The product searched
    private String productSearched;
    // Results list
    private List<Product> kilimallProducts;
    // The activity
    private Activity activity;

    //      Widgets
    @BindView(R.id.resultsRecyclerView) RecyclerView wKilimallRecyclerView;
    @BindView(R.id.progressBar) ProgressBar wProgressBar;
    @BindView(R.id.errorMessage) TextView wErrorMessage;
    @BindView(R.id.noResult) TextView wNoResult;

    public AlibabaFragment() {
        // Required empty public constructor
    }

    public static AlibabaFragment newInstance() {
        return new AlibabaFragment();
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

        // binding widgets
        ButterKnife.bind(this, kilimallView);

        // Get the context
        this.context = getContext();

        // Assigning activity
        this.activity = getActivity();

        // Getting the search input
        // Shared preferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        productSearched = sharedPreferences.getString(Constants.SEARCH_INPUT_KEY, null).trim();

        KilimallScraper kilimallScraper = new KilimallScraper();
        kilimallScraper.execute();

        return kilimallView;
    }

    // kilimall scraping class
    private class KilimallScraper extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: kilimall scrape init");

            OkHttpClient client = new OkHttpClient();
            String thisUrl = Constants.KILIMALL_BASE_URL + productSearched + Constants.KILIMALL_PAGE_NO;
            Request request = new Request.Builder().url(thisUrl).build();
            Log.d(TAG, "doInBackground: This url: " + thisUrl );
            try {
                Response response = client.newCall(request).execute();

                // print response
                Log.d(TAG, "doInBackground: Response " + response.body().string());

                response.close();
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: Response -------- ERROR");
                e.printStackTrace();
            }

            try {

                // init the array list
                kilimallProducts = new ArrayList<>();

                // Loop to get data from 2 pages
                for (int pageNumber = 1; pageNumber < 3; pageNumber += 1) {

                    // Url to be used in browser
                    String url = Constants.KILIMALL_BASE_URL + productSearched + Constants.KILIMALL_PAGE_NO + pageNumber;
                    final Document extractedContent = Jsoup.connect(url).get();

                    Handler kilimallHandler = new Handler();
                    kilimallHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // Confirming url
                            Log.d(TAG, "doInBackground: extracted content url " + url);

                            Elements dataObtained = extractedContent.select("div.grid-content.bg-purple.clearfix");

                            Log.d(TAG, "doInBackground: Data Obtained: -----------" + dataObtained);

                            if (dataObtained.size() > 0) {

                                int dataSize = dataObtained.size();

                                for (int j = 0; j < dataSize; j += 1) {

                                    String linkToPage = dataObtained.select("a.showHand")
                                            .eq(j)
                                            .attr("href");

                                    String nameFromUrl = dataObtained.select("div.wordwrap")
                                            .select("div")
                                            .eq(j)
                                            .text();

                                    String imageFromUrl = dataObtained.select("div.imgClass")
                                            .select("img.imgClass")
                                            .eq(j)
                                            .attr("src");

                                    String priceFromUrl = dataObtained.select("div.wordwrap-price")
                                            .select("span.")
                                            .eq(j)
                                            .text();

                                    kilimallProducts.add(new Product(linkToPage, nameFromUrl, priceFromUrl, "Rating here", imageFromUrl));
                                }

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        // Passing the products to the adapter
                                        passToAdapter(kilimallProducts);

                                        // Method to hide progress bar
                                        showResults();
                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!(wKilimallRecyclerView.isFocusable())) {
                                            noResult();
                                        }
                                    }
                                });
                            }
                        }

                    }, 5000);

                }
            } catch (Exception e) {

                System.out.println(e.getMessage());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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

        Snackbar.make(requireView(), "Swipe Down To Refresh!", Snackbar.LENGTH_SHORT)
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
        wKilimallRecyclerView.setVisibility(View.VISIBLE);
        wKilimallRecyclerView.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

    }

    public void passToAdapter(List<Product> retrievedProducts) {

        //    Local variables
        // Results adapter
        ResultItemAdapter resultItemAdapter = new ResultItemAdapter(retrievedProducts, "Kilimall", getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false);

        wKilimallRecyclerView.setAdapter(resultItemAdapter);
        wKilimallRecyclerView.setLayoutManager(gridLayoutManager);
        wKilimallRecyclerView.setClipToPadding(false);

    }
}