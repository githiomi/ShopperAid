package com.githiomi.onlineshoppingassistant.Fragments.Ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.parceler.Parcels;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AmazonDetailFragment extends Fragment {

    //    TAG
    private static final String TAG = AmazonDetailFragment.class.getSimpleName();

    //    Local variables
    // For the product
    private Product productToShowDetails;
    // For the url
    public String detailUrl;
    // For the warranty and delivery
    private String productDeliveryAndWarranty;
    // For the description
    private String productDescription;
    // For the activity
    private Activity activity;

    //    Widgets
    @BindView(R.id.productItemImage) ImageView wProductImage;
    @BindView(R.id.productItemName) TextView wProductName;
    @BindView(R.id.productItemPrice) TextView wProductPrice;
    @BindView(R.id.productItemRating) TextView wProductRating;
    @BindView(R.id.productWarranty) TextView wProductWarranty;
    @BindView(R.id.productSpecs) TextView wProductSpecs;
    @BindView(R.id.btnGoToSite) Button wToSite;
    @BindView(R.id.specsProgressBar) ProgressBar wSpecsProgressBar;
    @BindView(R.id.noResult) TextView wErrorMessage;
    @BindView(R.id.cvButtonToSite) CardView wButtonCardView;
    // Card views
    @BindView(R.id.cvProductDetailsTitle) CardView wProductSpecsTitle;
    @BindView(R.id.cvProductSpecs) CardView wProductSpecifications;


    public AmazonDetailFragment() {
        // Required empty public constructor
    }

    public static AmazonDetailFragment newInstance(Product product) {
        AmazonDetailFragment fragment = new AmazonDetailFragment();
        Bundle args = new Bundle();

        args.putParcelable(Constants.WRAP_PRODUCT, Parcels.wrap(product));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            // To get the data
            productToShowDetails = Parcels.unwrap(getArguments().getParcelable(Constants.WRAP_PRODUCT));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View productDetail = inflater.inflate(R.layout.fragment_detail, container, false);

        // Binding widgets
        ButterKnife.bind(this, productDetail);

        // Assigning activty
        this.activity = getActivity();

        // Setting views
        // Image
        int MAX_WIDTH = 230;
        int MAX_HEIGHT = 250;

        if (!(productToShowDetails.getImageUrl().isEmpty())) {
            Picasso.get().load(productToShowDetails.getImageUrl())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerInside()
                    .into(wProductImage);
        } else {
            Picasso.get().load(R.drawable.no_image)
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerInside()
                    .into(wProductImage);
        }

        if (productToShowDetails.getName().isEmpty()) {
            wProductName.setText("No product name to display");
        } else {
            wProductName.setText(productToShowDetails.getName());
        }

        if (productToShowDetails.getPrice().isEmpty()) {
            wProductPrice.setText("No price available");
        } else {
            wProductPrice.setText(productToShowDetails.getPrice());
        }

        if (productToShowDetails.getRating().isEmpty()) {
            wProductRating.setText("No rating available");
        } else {
            wProductRating.setText(productToShowDetails.getRating());
        }

        wToSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (detailUrl.isEmpty() || detailUrl.equals("")) {
                    String error = "Product does not exist";
                    Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                } else {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(detailUrl));
                    startActivity(webIntent);
                }
            }
        });

        // Scrape to get the details
        SecondaryScrape secondaryScrape = new SecondaryScrape();
        secondaryScrape.execute();

        return productDetail;
    }

    //    Method to scrape for details
    private class SecondaryScrape extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: SecondaryScrape init");

            try {
//            Assigning the new url
                detailUrl = Constants.AMAZON_DETAIL + productToShowDetails.getLink().trim();

//                Scrapping
                Document allObtainedData = Jsoup.connect(detailUrl).get();

                Elements dataObtained = allObtainedData.select("div#ppd");

                productDeliveryAndWarranty = dataObtained
                        .select("div.a-section.a-spacing-mini.a-spacing-top-micro")
                        .select("b")
                        .text();

                productDescription = dataObtained
                        .select("ul.a-unordered-list.a-vertical.a-spacing-mini")
                        .select("li")
                        .text();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Context context = getContext();

                        // Data obtained so hide bar and show details
                        wSpecsProgressBar.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_out));
                        wSpecsProgressBar.setVisibility(View.GONE);
                        wProductSpecifications.setVisibility(View.VISIBLE);
                        wProductSpecifications.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
                        wProductSpecsTitle.setVisibility(View.VISIBLE);
                        wProductSpecsTitle.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));
                        wButtonCardView.setVisibility(View.VISIBLE);
                        wButtonCardView.startAnimation(AnimationUtils.loadAnimation(activity, android.R.anim.fade_in));

                        if (productDeliveryAndWarranty.isEmpty()) {
                            wProductWarranty.setText(R.string.no_details);
                        } else {
                            String delivery = "Estimated between: " + productDeliveryAndWarranty;
                            wProductWarranty.setText(delivery);
                        }

                        if (productDescription.isEmpty()) {
                            wProductSpecs.setText(R.string.no_specs);
                        } else {
                            wProductSpecs.setText(productDescription);
                        }

                    }
                });

            } catch (Exception exception) {

                System.out.println(exception.getMessage());

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

    // In case connection is lost
    private void showUnsuccessful() {

        Context context = getContext();

        wSpecsProgressBar.setVisibility(View.GONE);
        wSpecsProgressBar.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
        wErrorMessage.setVisibility(View.VISIBLE);
        wErrorMessage.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));

        Snackbar.make(Objects.requireNonNull(getView()), "Swipe Down To Refresh!", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.colorPrimary))
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                .setAction("Action", null).show();

    }
}