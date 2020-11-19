package com.githiomi.onlineshoppingassistant.Fragments.Ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailFragment extends Fragment {

//    TAG
    private static final String TAG = DetailFragment.class.getSimpleName();

//    Local variables
    // For the product
    private Product productToShowDetails;
    // For the url
    public String detailUrl;
    // For the warranty and delivery
    private String productDeliveryAndWarranty;
    // For the description
    private String productDescription;

//    Widgets
    @BindView(R.id.productItemImage) ImageView wProductImage;
    @BindView(R.id.productItemName) TextView wProductName;
    @BindView(R.id.productItemPrice) TextView wProductPrice;
    @BindView(R.id.productItemRating) TextView wProductRating;
    @BindView(R.id.productWarranty) TextView wProductWarranty;
    @BindView(R.id.productSpecs) TextView wProductSpecs;
    @BindView(R.id.btnGoToSite) Button wToSite;


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance( Product product ) {
        DetailFragment fragment = new DetailFragment();
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
            productToShowDetails = Parcels.unwrap( getArguments().getParcelable(Constants.WRAP_PRODUCT) );
            Log.d(TAG, "newInstance: Product unwrapped from fragment " + productToShowDetails.getName() + "-------------" + productToShowDetails.getLink());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View productDetail = inflater.inflate(R.layout.fragment_detail, container, false);

        // Binding widgets
        ButterKnife.bind(this, productDetail);

        // Setting views
        // Image
        int MAX_WIDTH = 150;
        int MAX_HEIGHT = 200;

        Picasso.get().load(productToShowDetails.getImageUrl())
                .resize(MAX_WIDTH, MAX_HEIGHT)
                .centerCrop()
                .into(wProductImage);

        if ( productToShowDetails.getName().isEmpty() ){
            wProductName.setText("No product to display");
        } else {
            wProductName.setText(productToShowDetails.getName());
        }

        if ( productToShowDetails.getPrice().isEmpty() ) {
            wProductPrice.setText("No image available");
        } else {
            wProductPrice.setText(productToShowDetails.getPrice());
        }

        if ( productToShowDetails.getRating().isEmpty() ) {
            wProductRating.setText("No rating available");
        } else {
            wProductRating.setText(productToShowDetails.getRating());
        }

        Log.d(TAG, "Item url: itemUrl " + productToShowDetails.getLink() );

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
                detailUrl = Constants.JUMIA_ITEM_URL + productToShowDetails.getLink();

//                Scrapping
                Document allObtainedData = Jsoup.connect(detailUrl).get();

                Elements dataObtained = allObtainedData.select("main.-pvs");

                productDeliveryAndWarranty = dataObtained.select("div.row")
                        .select("div.-ptxs")
                        .select("span.markup")
                        .text();

                productDescription = dataObtained.select("div.row")
                        .select("div.col12")
                        .select("ul")
                        .text();

//                assign method
                final Activity productDetailActivity = getActivity();

                productDetailActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if ( productDeliveryAndWarranty.isEmpty() ) {
                            wProductWarranty.setText("NO WARRANTY OR DELIVERY DETAILS FOR THIS PRODUCT");
                        } else {
                            wProductWarranty.setText(productDeliveryAndWarranty);
                        }

                        if ( productDescription.isEmpty() ) {
                            wProductSpecs.setText("NO PRODUCT SPECS FOR THIS PRODUCT");
                        } else {
                            wProductSpecs.setText(productDescription);
                        }

                        wToSite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(detailUrl));
                                startActivity(webIntent);
                                Toast.makeText(getContext(), "Link: " + detailUrl, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            } catch (Exception exception) {

                System.out.println(exception.getMessage());

            }

            return null;
        }
    }
}