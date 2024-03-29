package com.githiomi.onlineshoppingassistant.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.githiomi.onlineshoppingassistant.Models.Constants;
import com.githiomi.onlineshoppingassistant.Models.Product;
import com.githiomi.onlineshoppingassistant.R;
import com.githiomi.onlineshoppingassistant.Ui.DetailActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.parceler.Parcels;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResultItemAdapter extends RecyclerView.Adapter<ResultItemAdapter.ResultItemViewHolder> {

//    TAG
    private static final String TAG = ResultItemAdapter.class.getSimpleName();

//    Local variables
    private final List<Product> productsRetrieved;
    private final String fragmentSource;
    private final Context context;

//    Constructor
    public ResultItemAdapter(List<Product> products, String fragmentSource, Context contextPassed){

        this.productsRetrieved = products;
        this.fragmentSource = fragmentSource;
        this.context = contextPassed;

    }

    @NonNull
    @Override
    public ResultItemAdapter.ResultItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View resultView = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item_view, parent, false);
        return new ResultItemViewHolder(resultView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultItemAdapter.ResultItemViewHolder holder, int position) {
        // Binding passed result
        Product product = productsRetrieved.get(position);
        try {
            holder.bindResultToView(product);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productsRetrieved.size();
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Image constants
        private static final int MAX_WIDTH = 150;
        private static final int MAX_HEIGHT = 200;

        // Widgets
        @BindView(R.id.productImageView) ImageView wProductImage;
        @BindView(R.id.productName) TextView wProductName;
        @BindView(R.id.productPrice) TextView wProductPrice;
        @BindView(R.id.tvInKsh) TextView wInKenyaShillings;
        @BindView(R.id.productRating) TextView wProductRating;

        public ResultItemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Click listener
            itemView.setOnClickListener(this);

            // Binding widgets
            ButterKnife.bind(this, itemView);

        }

        public void bindResultToView(Product product) throws IOException, JSONException {

            if ( !(product.getImageUrl().isEmpty()) ) {
                Picasso.get().load(product.getImageUrl())
                        .resize(MAX_WIDTH, MAX_HEIGHT)
                        .centerInside()
                        .into(wProductImage);
            }else{
                Picasso.get().load(R.drawable.no_image)
                        .resize(MAX_WIDTH, MAX_HEIGHT)
                        .centerInside()
                        .into(wProductImage);
            }

            wProductName.setText(product.getName());
            wProductPrice.setText(product.getPrice());

            if ( product.getPrice().contains("$") ){

                wInKenyaShillings.setVisibility(View.VISIBLE);

                String inDollars = product.getPrice().replaceAll("[$,]", "");
                float dollars = Float.parseFloat(inDollars);

                float ksh = (Constants.DOLLARS_TO_KSH * dollars);

                DecimalFormat df = new DecimalFormat("###,###,###,###.00");
                String decimal = String.valueOf(df.format(ksh));
                wProductPrice.setText(product.getPrice());
                String toKes = "KES " + decimal;
                wInKenyaShillings.setText(toKes);

            }else {
                wInKenyaShillings.setVisibility(View.GONE);
                wProductPrice.setText(product.getPrice());
            }

            if ( !(product.getRating().isEmpty()) ) {
                wProductRating.setText(product.getRating());
            }else {
                wProductRating.setText(R.string.no_rating);
            }
        }

        // What happens when a single item is clicked
        @Override
        public void onClick(View v) {

            int itemPosition = getLayoutPosition();

            if ( v == itemView ) {

                Intent toDetailActivity = new Intent(context, DetailActivity.class);
                toDetailActivity.putExtra(Constants.WRAP_PRODUCT, Parcels.wrap(productsRetrieved));
                toDetailActivity.putExtra(Constants.ITEM_POSITION, itemPosition);
                toDetailActivity.putExtra(Constants.FRAGMENT_SOURCE, fragmentSource);
                context.startActivity(toDetailActivity);

            }
        }
    }
}